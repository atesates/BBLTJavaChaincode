package producttransfer;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.owlike.genson.Genson;



@Contract(name = "ProductTransfer", info = @Info(title = "ProductTransfer contract", description = "A Sample product transfer chaincode example", version = "0.0.1-SNAPSHOT"))

@Default
public final class ProductTransfer implements ContractInterface {

	private final Genson genson = new Genson();

	private enum ProductTransferErrors {
		PRODUCT_NOT_FOUND, PRODUCT_ALREADY_EXISTS, SUPPLY_NOT_ENOUGH
	}

	/**
	 * Add some initial properties to the ledger
	 *
	 * @param ctx the transaction context
	 */
	@Transaction()
	public void initLedger(final Context ctx) {

		ChaincodeStub stub = ctx.getStub();

		Product product = new Product("FirstOwner_FirstProduct_00.00.2000", 
				"FirstProduct_00.00.2000", "FirstProduct", "FirstOwner", 
				"100", "10", "02.02.2020", "01.01.2199",
				"on sale", "FirstOwner", "00.00.2000", " ");

		String productState = genson.serialize(product);

		stub.putStringState("1", productState);
	}

	/**
	 * Add new product on the ledger.
	 *
	 * @param ctx              the transaction context
	 * @param id               the key for the new product
	 * @param productId        the id for the new product
	 * @param name             the name of the new product
	 * @param numberOf         the number of the new product
	 * @param ownername        the owner of the new product
	 * @param value            the value of the new product
	 * @param manufacturedDate the manufacturedDate of the new product
	 * @param expirationDate   the expirationDate of the new product
	 * @param issueDate        the issueDate of the new product
	 * @param supplier         the supplier of the new product
	 * @param demander         the demander of the new product
	 * @param status           the status of the new product, on sale or purchased
	 * @return the created product
	 */

	@Transaction()
	public Product addNewProduct(final Context ctx, final String id, final String productId, final String name, final String numberOf,
			final String ownername, final String value, final String manufacturedDate, final String expirationDate,
			final String status, final String supplyer, final String issueDate, final String demander) {

		ChaincodeStub stub = ctx.getStub();

		String productState = stub.getStringState(id);

		if (!productState.isEmpty()) {
			String errorMessage = String.format("Product %s already exists", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_ALREADY_EXISTS.toString());
		}

		Product product = new Product(id, productId, name, ownername, numberOf, value, manufacturedDate, expirationDate, status,
				supplyer, issueDate, demander);

		productState = genson.serialize(product);

		stub.putStringState(id, productState);

		return product;
	}

	/**
	 * Retrieves a product based upon product Id from the ledger.
	 *
	 * @param ctx the transaction context
	 * @param id  the key
	 * @return the product found on the ledger if there was one
	 */
	@Transaction()
	public Product queryProductById(final Context ctx, final String id) {
		ChaincodeStub stub = ctx.getStub();
		String productState = stub.getStringState(id);

		if (productState.isEmpty()) {
			String errorMessage = String.format("Product %s does not exist", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
		}

		Product product = genson.deserialize(productState, Product.class);
		return product;
	}

	/**
	 * change product owner
	 *
	 * @param ctx      the transaction context
	 * @param id       the key
	 * @param newOwner the new owner
	 * @return the updated product
	 */
	@Transaction()
	public Product changeProductOwnership(final Context ctx, final String id, final String newProductOwner) {
		ChaincodeStub stub = ctx.getStub();

		String productState = stub.getStringState(id);
		// Integer remaining = Integer.parseInt(supply.getNumberOf()) -
		// Integer.parseInt(numberOf);

		if (productState.isEmpty()) {
			String errorMessage = String.format("Product %s does not exist", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
		}

		Product product = genson.deserialize(productState, Product.class);

		Product newProduct = new Product(product.getId(),product.getProductId(), product.getName(), newProductOwner, product.getValue(),
				product.getNumberOf(), product.getManufacturedDate(), product.getExpirationDate(), "changed",
				product.getSupplier(), product.getIssueDate(), product.getDemander());

		String newProductState = genson.serialize(newProduct);
		stub.putStringState(id, newProductState);

		return newProduct;
	}

	/**
	 * Delete a product
	 *
	 * @param ctx the transaction context
	 * @param id  the key
	 * @return the updated product
	 */
	@Transaction()
	public String deleteProduct(final Context ctx, final String id) {
		ChaincodeStub stub = ctx.getStub();

		String productState = stub.getStringState(id);

		if (productState.isEmpty()) {
			String errorMessage = String.format("Product %s does not exist", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
		}

		stub.delState(id);

		return "Product deleted successfully";
	}

	/**
	 * Subtract from product on sale on the ledger. And update the number of
	 * purchase product
	 *
	 * @param ctx      the transaction context
	 * @param id       the key
	 * @param newOwner the new owner
	 * @return the updated product
	 */
	@Transaction()
	public Product purchaseSomeProduct(final Context ctx, final String id, final String newProductOwner,
			final String numberOfPurchased) {
		ChaincodeStub stub = ctx.getStub();

		String productState = stub.getStringState(id);

		if (productState.isEmpty()) {
			String errorMessage = String.format("Product %s does not exist", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
		}

		Product product = genson.deserialize(productState, Product.class);

		Integer remaining = Integer.parseInt(product.getNumberOf()) - Integer.parseInt(numberOfPurchased);

		Product newProduct = new Product(product.getId(), product.getProductId(), product.getName(), product.getOwner(), product.getValue(),
				remaining.toString(), product.getManufacturedDate(), product.getExpirationDate(), "on sale",
				product.getSupplier(), product.getIssueDate(), product.getDemander());
		
		if (remaining > 0) {// if the number of the supply is enough for purchase
			
			String newProductState = genson.serialize(newProduct);
            //update supply
			stub.putStringState(id, newProductState);
			
			Product newProduct2 = new Product(product.getOwner() + product.getName() + product.getIssueDate(),
					product.getProductId(), 
					product.getName(), newProductOwner, product.getValue(),
					numberOfPurchased.toString(), product.getManufacturedDate(), 
					product.getExpirationDate(), "purchased",
					product.getOwner(), product.getIssueDate(), newProductOwner);

			String newProductState2 = genson.serialize(newProduct2);
			//create purchase
			stub.putStringState(id, newProductState2);

			return newProduct2;

		} else if(remaining == 0) {//all product is purchased
			//delete supply		
			stub.delState(id);
			
			Product newProduct2 = new Product(product.getOwner() + product.getName() + product.getIssueDate(),
					product.getProductId(), 
					product.getName(), newProductOwner, product.getValue(),
					numberOfPurchased.toString(), product.getManufacturedDate(), 
					product.getExpirationDate(), "purchased",
					product.getOwner(), product.getIssueDate(), newProductOwner);

			String newProductState2 = genson.serialize(newProduct2);
			//create purchase
			stub.putStringState(id, newProductState2);
			
			return newProduct2;			
		}
		else {//intended to be purchased product is more than supply
			String errorMessage = String.format("Supply %s is not enough", id);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, ProductTransferErrors.SUPPLY_NOT_ENOUGH.toString());
		}

	}
}