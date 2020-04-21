package uk.me.jasonmarston.domain.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;

import uk.me.jasonmarston.domain.aggregate.Account;
import uk.me.jasonmarston.domain.factory.entity.TransactionBuilderFactory;
import uk.me.jasonmarston.domain.value.Amount;
import uk.me.jasonmarston.domain.value.TransactionType;
import uk.me.jasonmarston.framework.domain.builder.IBuilder;
import uk.me.jasonmarston.framework.domain.entity.AbstractEntity;
import uk.me.jasonmarston.framework.domain.type.impl.EntityId;

@Entity(name = "TRANSACTIONS")
public class Transaction extends AbstractEntity {
	public static class Builder implements IBuilder<Transaction> {
		private TransactionType type;
		private Account account;
		private Amount amount;
		private EntityId referenceAccountId;
		private String description;

		private Builder() {
		}

		public Builder againstAccount(Account account) {
			this.account = account;
			return this;
		}

		@Override
		public Transaction build() {
			if(type == null || account == null || amount == null) {
				throw new IllegalArgumentException("Invalid transaction.");
			}

			final Transaction transaction = new Transaction();
			transaction.type = type;
			transaction.account = account;
			transaction.amount = amount;
			transaction.description = description;
			transaction.referenceAccountId = referenceAccountId;

			return transaction;
		}

		public Builder forAmount(Amount amount) {
			this.amount = amount;
			return this;
		}

		public Builder ofType(TransactionType type) {
			this.type = type;
			return this;
		}
		
		public Builder withDescrption(String description) {
			this.description = description;
			return this;
		}

		public Builder withReferenceAccountId(
				final EntityId referenceAccountId) {
			this.referenceAccountId = referenceAccountId;
			return this;
		}
	}

	@Service
	public static class FactoryImpl implements TransactionBuilderFactory {
		@Override
		public Builder create() {
			return new Builder();
		}
	}

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private TransactionType type;

	@ManyToOne(cascade = CascadeType.ALL)
	@AttributeOverride(name="id", column=@Column(name="ownerAccountId"))
	@NotNull
	private Account account;

	@NotNull
	@Valid
	private Amount amount;
	
	private String description;
	
	@NotNull
	@Column(columnDefinition="TIMESTAMP")
	private ZonedDateTime dateTime;

	@AttributeOverride(name="id", column=@Column(name="referenceAccountId", columnDefinition = "CHAR(36)"))
	private EntityId referenceAccountId;

	private Transaction() {
		super();
		final Instant now = Instant.now();
		final ZoneId utc = ZoneId.of("UTC");
		dateTime = ZonedDateTime.ofInstant(now, utc);
	}

	@Override
	protected ToStringBuilder _addFieldsToToString() {
		return _getToStringBuilder()
				.append("accountId", account.getId());
	}

	@Override
	protected String[] _getExcludeFromUniqueness() {
		return new String[] { "account" };
	}

	public Account getAccount() {
		return account;
	}

	public Amount getAmount() {
		return amount;
	}

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public String getDescription() {
		return description;
	}

	public EntityId getReferenceAccountId() {
		return referenceAccountId;
	}

	public TransactionType getType() {
		return type;
	}
}