package by.spk.price.web;

import by.spk.price.JdbcConnection;
import by.spk.price.entity.WebProduct;
import by.spk.price.entity.WebPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebDAO {

//    private static final String PRICE = "select prices.id as id, subcategory, item, product," +
//            " recommendedprice, lastprice, percent, product_id from prices" +
//            " inner join products on prices.product_id = products.id" +
//            " inner join subcategories on prices.subcategory_id = subcategories.id" +
//            " OFFSET ? LIMIT ?;";

    /*private static final String PRICE_WHERE = "select prices.id as id, subcategory, item, product," +
            " recommendedprice, lastprice, percent, product_id from prices" +
            " inner join products on prices.product_id = products.id" +
            " inner join subcategories on prices.subcategory_id = subcategories.id" +
            " where LOWER(product) like LOWER(?)" +
            " OFFSET ? LIMIT ?;";*/

    private static final String PRICE_WHERE = "select prices.id as id, subcategory, item, product,"
            + " recommendedprice, lastprice, percent, product_id from prices"
            + " inner join products on prices.product_id = products.id"
            + " inner join subcategories on prices.subcategory_id = subcategories.id"
            + " where LOWER(product) like LOWER(?);";

    private static final String UPDATE_PRICE = "update products set lastprice = ?, percent = ? where id = ?;";
    private static final String SELECT_VALUE = "select val from data where key = ?;";

    public WebDAO() {
    }

    protected Connection getConnection() {
        return JdbcConnection.getInstance();
    }

    protected void close(final PreparedStatement statement, final ResultSet resultSet) {
        JdbcConnection.close(statement, resultSet);
    }

    public void savePrices(final List<WebProduct> products) {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(UPDATE_PRICE);

            for (WebProduct prod : products) {
                statement.setDouble(1, prod.getLastPrice());
                statement.setInt(3, prod.getId());
                statement.setDouble(2, prod.getPercent());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error set new prices: " + e.getMessage());
        } finally {
            close(statement, null);
        }
    }

    public String getValue(final String key) {
        String value = "";
        PreparedStatement statement = null;
        ResultSet rows = null;

        try {
            statement = getConnection().prepareStatement(SELECT_VALUE);

            statement.setString(1, key);
            statement.execute();
            rows = statement.getResultSet();
            while (rows.next()) {
                value = rows.getString(1);
            }
            return value;

        } catch (SQLException e) {
            throw new IllegalArgumentException("Error get Value by Key(" + key + "): " + e.getMessage());
        } finally {
            close(statement, rows);
        }
    }

    /*public List<WebPrice> show(int offset, int limit, String where) {
        List<WebPrice> prices = new ArrayList<>();
        PreparedStatement statement = null;

        try {
                    priceStatement = getConnection().prepareStatement(PRICE);
            ResultSet rows;
            if (where == null || where.isEmpty()) {
                priceStatement.setInt(1, offset);
                priceStatement.setInt(2, limit);
                priceStatement.execute();
                rows = priceStatement.getResultSet();
            } else {
                priceWhereStatement.setString(1, "%" + where + "%");
                priceWhereStatement.setInt(2, offset);
                priceWhereStatement.setInt(3, limit);
                priceWhereStatement.execute();
                rows = priceWhereStatement.getResultSet();
            }

            WebPrice price;

            while (rows.next()) {

                price = new WebPrice();

                price.setPercent(rows.getDouble("percent"));
                price.setLastPrice(rows.getDouble("lastprice"));
                price.setIdProd(rows.getInt("product_id"));
                price.setIdPrice(rows.getInt(1));
                price.setProduct(rows.getString("product"));
                price.setItem(rows.getString("item"));
                price.setRecommendedPrice(rows.getDouble("recommendedprice"));

                price.setOurPrice(new BigDecimal(price.getRecommendedPrice() * 0.77)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue());

                prices.add(price);
            }
        return prices;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error get Prices: " + e.getMessage());
        }

        finally {
            close(statement, rows);
        }
    }*/

    public List<WebPrice> show(final String where) {
        List<WebPrice> prices = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = getConnection().prepareStatement(PRICE_WHERE);

            statement.setString(1, "%" + where + "%");
            statement.execute();
            rows = statement.getResultSet();

            WebPrice price;

            while (rows.next()) {

                price = new WebPrice();

                price.setPercent(rows.getDouble("percent"));
                price.setLastPrice(rows.getDouble("lastprice"));
                price.setIdProd(rows.getInt("product_id"));
                price.setIdPrice(rows.getInt(1));
                price.setProduct(rows.getString("product"));
                price.setItem(rows.getString("item"));
                price.setRecommendedPrice(rows.getDouble("recommendedprice"));

                price.setOurPrice(new BigDecimal(price.getRecommendedPrice() * 0.77)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue());

                prices.add(price);
            }
            return prices;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error get Prices: " + e.getMessage());
        } finally {
            close(statement, rows);
        }
    }
}
