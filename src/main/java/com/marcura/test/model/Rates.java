package com.marcura.test.model;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "rates",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"currency", "date"})}
)
public class Rates {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String currency;
  private BigDecimal conversion;
  private LocalDate date;
  private Long counter;

  @Version
  private Long version;

  public Rates(String currency, BigDecimal conversion, LocalDate date, Long counter) {
    this.currency = currency;
    this.conversion = conversion;
    this.date = date;
    this.counter = counter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Rates rates = (Rates) o;

    if (!currency.equals(rates.currency)) return false;
    return date.equals(rates.date);
  }

  @Override
  public int hashCode() {
    int result = currency.hashCode();
    result = 31 * result + date.hashCode();
    return result;
  }
}
