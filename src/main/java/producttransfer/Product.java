package producttransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import java.util.Objects;

@DataType()
public final class Product {

	@Property()
	private final String id;

	@Property()
	private final String name;

	@Property()
	private final String owner;

	@Property()
	private final String value;

	@Property()
	private final String numberOf;

	@Property()
	private final String expirationDate;

	@Property()
	private final String manufacturedDate;
	
	@Property()
	private final String status;

	@Property()
	private final String issueDate;
	
	@Property()
	private final String supplyer;
	
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getValue() {
		return value;
	}

	public String getNumberOf() {
		return numberOf;
	}

	public String getManufacturedDate() {
		return manufacturedDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getIssueDate() {
		return issueDate;
	}
	
	public String getSupplyer() {
		return supplyer;
	}
	
	public Product(@JsonProperty("id") final String id, 
			@JsonProperty("name") final String name,
			@JsonProperty("owner") final String owner, 
			@JsonProperty("value") final String value,
			@JsonProperty("numberOf") final String numberOf,
			@JsonProperty("manufacturedDate") final String manufacturedDate,
			@JsonProperty("expirationDate") final String expirationDate,
			@JsonProperty("status") final String status,
			@JsonProperty("supplyer") final String supplyer,
			@JsonProperty ("issueDate")final String issueDate) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.value = value;
		this.numberOf = numberOf;
		this.manufacturedDate = manufacturedDate;
		this.expirationDate = expirationDate;
		this.status = status;
		this.supplyer = supplyer;
		this.issueDate = issueDate;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		Product other = (Product) obj;

		return Objects.deepEquals(
				new String[] { getId(), getName(), getOwner(), getValue(), getManufacturedDate(), getExpirationDate() },
				new String[] { other.getId(), other.getName(), other.getOwner(), other.getValue(), other.getOwner(),
						other.getNumberOf(), other.getManufacturedDate(), other.getExpirationDate(), other.getStatus(),
						other.getSupplyer(), other.getIssueDate()});
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getOwner(), getValue(), getNumberOf(), getManufacturedDate(),
				getExpirationDate(), getStatus(), getSupplyer(), getIssueDate());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [id=" + id + ", name=" + name
				+ ", owner=" + owner + ", value=" + value + " , numberOf=" + numberOf + " , manufacturedDate="
				+ manufacturedDate + ", expirationDate=" + expirationDate + ", status=" + status 
				+ ", supplyer=" + supplyer + ", issueDate=" + issueDate + "]";
	}
}
