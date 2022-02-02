package io.andsanchez.bank.validator;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import io.andsanchez.bank.exceptions.ValidationException;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import reactor.core.publisher.Mono;

@Component
public class EntityValidator {

  private final Validator validator;

  public EntityValidator() {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final MessageInterpolator defaultInterpolator = factory.getMessageInterpolator();
    final MessageInterpolator interpolator = new LocaleContextMessageInterpolator(defaultInterpolator) {
      @Override
      public String interpolate(final String message, final Context context) {
        return this.interpolate(message, context, Locale.ENGLISH);
      }
    };

    this.validator = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(interpolator)
        .buildValidatorFactory()
        .getValidator();

    factory.close();
  }

  public <T> Mono<T> validate(final T entity) {
    return Mono.just(entity)
        .handle((event, sink) -> {
          final Set<ConstraintViolation<T>> violations = this.validator.validate(entity);
          if (violations.isEmpty()) {
            sink.next(entity);
          } else {
            sink.error(new ValidationException(this.joinConstraintViolations(violations)));
          }
        });
  }

  private <T> String joinConstraintViolations(final Set<ConstraintViolation<T>> violations) {
    return violations.stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(","));
  }

}
