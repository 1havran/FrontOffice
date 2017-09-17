package sk.hppa.frontoffice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Blob;
import java.util.ArrayList;


public class FrontOfficeDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "FrontOffice.db";

    public FrontOfficeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FrontOfficeDb.SQL_CUSTOMERS_CREATE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_GOODS_CREATE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_TNX_CREATE_ENTRIES);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FrontOfficeDb.SQL_CUSTOMERS_DELETE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_GOODS_DELETE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_TNX_DELETE_ENTRIES);
        onCreate(db);
    }
    public void cleanDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(FrontOfficeDb.SQL_CUSTOMERS_DELETE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_GOODS_DELETE_ENTRIES);
        db.execSQL(FrontOfficeDb.SQL_TNX_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public boolean insertCustomer(String customer, String customerFOID, String picPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST, customer);
        values.put(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST_FOID, customerFOID);
        if (picPath != null) {
            values.put(FrontOfficeDb.tbCustomer.COLUMN_NAME_PICTURE_PATH, picPath);
        }
        db.insert(FrontOfficeDb.tbCustomer.TABLE_NAME, null, values);
        return true;
    }

    public ArrayList getCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList custList = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FrontOfficeDb.tbCustomer.TABLE_NAME +
                " ORDER BY " + FrontOfficeDb.tbCustomer._ID + " ASC", null);
        while (cursor.moveToNext()) {
            String s = cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST));
            custList.add(s);
        }
        cursor.close();
        return custList;
    }
    public int updateCustomerByID (String custID, String custName, String picPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FrontOfficeDb.tbCustomer._ID + " = ?";
        String[] selectionArgs = { custID };
        ContentValues values = new ContentValues();
        values.put(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST, custName);
        if (picPath != null) {
            values.put(FrontOfficeDb.tbCustomer.COLUMN_NAME_PICTURE_PATH, picPath);
        }
        int count;
        count = db.update(FrontOfficeDb.tbCustomer.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }

    public ArrayList getCustomerIDsByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList alIds = new ArrayList();
        String sql = "select " + FrontOfficeDb.tbCustomer._ID + " from " + FrontOfficeDb.tbCustomer.TABLE_NAME;
        if (name != null) {
            sql = sql + " where " + FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST + " LIKE '" + name + "'";
        }
        Cursor cursor = db.rawQuery(sql + " order by " + FrontOfficeDb.tbCustomer._ID, null);
        while (cursor.moveToNext()) {
            alIds.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer._ID)));
        }
        cursor.close();
        return alIds;
    }

    public ArrayList getCustomerByID(String custID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList custList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from " + FrontOfficeDb.tbCustomer.TABLE_NAME +
                " where " + FrontOfficeDb.tbCustomer._ID + " = " + custID, null);
        while (cursor.moveToNext()) {
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer._ID)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST_FOID)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbCustomer.COLUMN_NAME_PICTURE_PATH)));
        }
        cursor.close();
        return custList;
    }

    public boolean insertGoods(String goods, String goodsFOID, Double quantity, String unit, String picAgreementPath, String picGoodsPath) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS, goods);
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS_FOID, goodsFOID);
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY, quantity);
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_UNIT, unit);
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_AGREEMENT_PATH, picAgreementPath);
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_PICTURE_PATH, picGoodsPath);
            db.insert(FrontOfficeDb.tbGoods.TABLE_NAME, null, values);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public ArrayList getGoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList result = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT " + FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS
                + " FROM " + FrontOfficeDb.tbGoods.TABLE_NAME
                + " ORDER BY " + FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS + " ASC", null);
        while (cursor.moveToNext()) {
            String s = cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS));
            result.add(s);
        }
        cursor.close();
        return result;
    }

    public ArrayList getGoodsIDsByName(String GoodsName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList alIds = new ArrayList();
        String sql = "select " + FrontOfficeDb.tbGoods._ID + " from " + FrontOfficeDb.tbGoods.TABLE_NAME;
        if (GoodsName != null) {
            sql = sql + " where " + FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS + " LIKE '" + GoodsName + "'";
        }
        Cursor cursor = db.rawQuery(sql + " order by " + FrontOfficeDb.tbGoods._ID, null);
        while (cursor.moveToNext()) {
            alIds.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods._ID)));
        }
        cursor.close();
        return alIds;
    }

    public ArrayList getGoodsByID(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList custList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from " + FrontOfficeDb.tbGoods.TABLE_NAME +
                " where " + FrontOfficeDb.tbGoods._ID + " = " + ID, null);
        while (cursor.moveToNext()) {
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods._ID)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_UNIT)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_AGREEMENT_PATH)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_PICTURE_PATH)));
            custList.add(cursor.getString(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS_FOID)));
        }
        cursor.close();
        return custList;
    }

    public Double getAvailableGoodsQuantity(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY +
                " from " + FrontOfficeDb.tbGoods.TABLE_NAME +
                " where " + FrontOfficeDb.tbGoods._ID + "= " + ID, null);
        Double result = null;
        while (cursor.moveToNext()) {
            result = cursor.getDouble(cursor.getColumnIndexOrThrow(FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY));
        }
        cursor.close();
        return result;
    }

    public int updateGoods(String goodsID, String foid, Double quantity, String unit, String picPath, String goodsPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FrontOfficeDb.tbGoods._ID + " = ?";
        String[] selectionArgs = { goodsID };
        ContentValues values = new ContentValues();
        values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY, quantity.toString());
        if (picPath != null) {
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_PICTURE_PATH, picPath);
        }
        if (goodsPath != null) {
            values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_AGREEMENT_PATH, goodsPath);
        }
        values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_UNIT, unit);
        values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS_FOID, foid);
        int count;
        count = db.update(FrontOfficeDb.tbGoods.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }
    public int updateQuantityOfGoods(String goodsID, Double quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FrontOfficeDb.tbGoods._ID + " = ?";
        String[] selectionArgs = { goodsID };
        ContentValues values = new ContentValues();
        values.put(FrontOfficeDb.tbGoods.COLUMN_NAME_QUANTITY, quantity.toString());
        int count;
        count = db.update(FrontOfficeDb.tbGoods.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }
    public boolean insertTnx(String sellerID, String buyerID, String goodsID, Double quantity,
                             String unit, String desc, String tnxFOID, String sellerPrice, String buyerPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_SELLERID, sellerID);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_BUYERID, buyerID);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_DESC, desc);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_GOODSID, goodsID);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_QUANTITY, quantity);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_UNIT, unit);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_SELLERPRICE, sellerPrice);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_BUYERPRICE, buyerPrice);
        values.put(FrontOfficeDb.tbTransactions.COLUMN_NAME_TRANS_FOID, tnxFOID);
        db.insert(FrontOfficeDb.tbTransactions.TABLE_NAME, null, values);
        return true;
    }

    public ArrayList getTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList result = new ArrayList();
        String sql = "SELECT t." + FrontOfficeDb.tbTransactions._ID + " AS id," +
                " s." + FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST + " AS seller," +
                " b." + FrontOfficeDb.tbCustomer.COLUMN_NAME_CUST + " AS buyer," +
                " g." + FrontOfficeDb.tbGoods.COLUMN_NAME_GOODS + " AS goods," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_QUANTITY + " AS quantity," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_UNIT + " AS unit," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_SELLERPRICE + " AS sellerPrice," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_BUYERPRICE + " AS buyerPrice," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_DESC + " AS description," +
                " t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_TRANS_FOID + " AS foid" +

                " FROM " + FrontOfficeDb.tbTransactions.TABLE_NAME + " AS t" +
                " INNER JOIN " + FrontOfficeDb.tbCustomer.TABLE_NAME + " AS s ON" +
                " s." + FrontOfficeDb.tbCustomer._ID + " = t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_SELLERID +
                " INNER JOIN " + FrontOfficeDb.tbCustomer.TABLE_NAME + " AS b ON" +
                " b." + FrontOfficeDb.tbCustomer._ID + " = t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_BUYERID +
                " INNER JOIN " + FrontOfficeDb.tbGoods.TABLE_NAME + " AS g ON" +
                " g." + FrontOfficeDb.tbGoods._ID + " = t." + FrontOfficeDb.tbTransactions.COLUMN_NAME_GOODSID;
        sql = sql + " ORDER BY id DESC";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ArrayList data = new ArrayList();
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("seller")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("buyer")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("goods")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("quantity")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("unit")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("sellerPrice")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("buyerPrice")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            data.add(cursor.getString(cursor.getColumnIndexOrThrow("foid")));
            result.add(data);
        }
        cursor.close();
        return result;
    }
}

final class FrontOfficeDb {

    private FrontOfficeDb() {}

    static class tbCustomer implements BaseColumns {
        static final String TABLE_NAME = "customers";
        static final String COLUMN_NAME_CUST = "customer";
        static final String COLUMN_NAME_CUST_FOID = "customerFOID";
        static final String COLUMN_NAME_PICTURE_PATH = "customerPicPath";
    }
    static final String SQL_CUSTOMERS_CREATE_ENTRIES =
            "CREATE TABLE " + tbCustomer.TABLE_NAME + " (" +
                    tbCustomer._ID + " INTEGER PRIMARY KEY," +
                    tbCustomer.COLUMN_NAME_CUST + " TEXT," +
                    tbCustomer.COLUMN_NAME_PICTURE_PATH + " TEXT," +
                    tbCustomer.COLUMN_NAME_CUST_FOID + " TEXT)";

    static final String SQL_CUSTOMERS_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + tbCustomer.TABLE_NAME;

    static class tbGoods implements BaseColumns {
        static final String TABLE_NAME = "goods";
        static final String COLUMN_NAME_GOODS = "goods";
        static final String COLUMN_NAME_GOODS_FOID = "goodsFOID";
        static final String COLUMN_NAME_QUANTITY = "quantity";
        static final String COLUMN_NAME_UNIT = "unit";
        static final String COLUMN_NAME_AGREEMENT_PATH = "agreement";
        static final String COLUMN_NAME_PICTURE_PATH = "picture";
    }
    static final String SQL_GOODS_CREATE_ENTRIES =
            "CREATE TABLE " + tbGoods.TABLE_NAME + " (" +
                    tbGoods._ID + " INTEGER PRIMARY KEY," +
                    tbGoods.COLUMN_NAME_GOODS + " TEXT," +
                    tbGoods.COLUMN_NAME_GOODS_FOID + " TEXT," +
                    tbGoods.COLUMN_NAME_QUANTITY + " REAL," +
                    tbGoods.COLUMN_NAME_UNIT + " TEXT," +
                    tbGoods.COLUMN_NAME_AGREEMENT_PATH + " TEXT," +
                    tbGoods.COLUMN_NAME_PICTURE_PATH + " TEXT)";

    static final String SQL_GOODS_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + tbGoods.TABLE_NAME;

    static class tbTransactions implements BaseColumns {
        static final String TABLE_NAME = "transactions";
        static final String COLUMN_NAME_SELLERID = "sellerId";
        static final String COLUMN_NAME_BUYERID = "buyerId";
        static final String COLUMN_NAME_GOODSID = "goodsId";
        static final String COLUMN_NAME_QUANTITY = "quantity";
        static final String COLUMN_NAME_UNIT = "unit";
        static final String COLUMN_NAME_SELLERPRICE = "sellerPrice";
        static final String COLUMN_NAME_BUYERPRICE = "buyerPrice";
        static final String COLUMN_NAME_TRANS_FOID = "transactionFOID";
        static final String COLUMN_NAME_DESC = "description";
    }

    static final String SQL_TNX_CREATE_ENTRIES =
            "CREATE TABLE " + tbTransactions.TABLE_NAME + " (" +
                    tbTransactions._ID + " INTEGER PRIMARY KEY," +
                    tbTransactions.COLUMN_NAME_SELLERID + " INTEGER," +
                    tbTransactions.COLUMN_NAME_BUYERID + " INTEGER," +
                    tbTransactions.COLUMN_NAME_GOODSID + " INTEGER," +
                    tbTransactions.COLUMN_NAME_QUANTITY + " REAL," +
                    tbTransactions.COLUMN_NAME_SELLERPRICE + " REAL," +
                    tbTransactions.COLUMN_NAME_BUYERPRICE + " REAL," +
                    tbTransactions.COLUMN_NAME_UNIT + " TEXT," +
                    tbTransactions.COLUMN_NAME_TRANS_FOID + " TEXT," +
                    tbTransactions.COLUMN_NAME_DESC + " TEXT)";

    static final String SQL_TNX_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + tbTransactions.TABLE_NAME;

}

