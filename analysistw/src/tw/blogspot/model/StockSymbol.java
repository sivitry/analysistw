package tw.blogspot.model;

public class StockSymbol extends Object{
	
	private int id;
	private int year;
	private double revenueQ1;
	private double revenueQ2;
	private double revenueQ3;
	private double revenueQ4;
	private double price;
	private double cashDividend;
	private double stockDividend;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getRevenueQ1() {
		return revenueQ1;
	}
	public void setRevenueQ1(double revenueQ1) {
		this.revenueQ1 = revenueQ1;
	}
	public double getRevenueQ2() {
		return revenueQ2;
	}
	public void setRevenueQ2(double revenueQ2) {
		this.revenueQ2 = revenueQ2;
	}
	public double getRevenueQ3() {
		return revenueQ3;
	}
	public void setRevenueQ3(double revenueQ3) {
		this.revenueQ3 = revenueQ3;
	}
	public double getRevenueQ4() {
		return revenueQ4;
	}
	public void setRevenueQ4(double revenueQ4) {
		this.revenueQ4 = revenueQ4;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getCashDividend() {
		return cashDividend;
	}
	public void setCashDividend(double cashDividend) {
		this.cashDividend = cashDividend;
	}
	public double getStockDividend() {
		return stockDividend;
	}
	public void setStockDividend(double stockDividend) {
		this.stockDividend = stockDividend;
	}
	
}
