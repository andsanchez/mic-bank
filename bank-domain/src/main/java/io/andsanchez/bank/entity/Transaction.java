package io.andsanchez.bank.entity;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Transaction implements Serializable {

  private static final long serialVersionUID = -4391653074566737307L;

  @NotNull
  String reference;

  @NotNull
  String accountIban;

  Long date;

  @NotNull
  Double amount;

  Double fee;

  String description;

}
