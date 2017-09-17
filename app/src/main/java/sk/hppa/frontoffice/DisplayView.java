package sk.hppa.frontoffice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayView extends Activity {
    final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(DisplayView.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Intent i = getIntent();
        String type = i.getStringExtra("type");

        if (type.equals("customers")) {
            populateCustomers();
        } else if (type.equals("goods")) {
            populateGoods();
        } else if (type.equals("transactions")) {
            populateTnx();
        }
    }

    private void populateCustomers() {
        ArrayList customerIDs = mDbHelper.getCustomerIDsByName(null);
        TableLayout tl = (TableLayout) findViewById(R.id.tlTableLayout);
        TableRow row = new TableRow(this);
        TextView tvID = new TextView(this);
        TextView tvCust = new TextView(this);

        tvID.setText("ID");
        row.addView(tvID);
        tvCust.setText("Customer");
        row.addView(tvCust);
        tl.addView(row);

        for (Object customerID : customerIDs) {
            TableRow tbrow = new TableRow(this);
            TextView tv0 = new TextView(this);
            TextView tv1 = new TextView(this);

            ArrayList customerData = mDbHelper.getCustomerByID(customerID.toString());
            tv0.setText(customerData.get(0).toString());
            tv1.setText(customerData.get(1).toString());

            tbrow.addView(tv0);
            tbrow.addView(tv1);
            tl.addView(tbrow);
        }
    }
    private void populateGoods() {
        ArrayList goodsIDs = mDbHelper.getGoodsIDsByName(null);

        TableLayout tl = (TableLayout) findViewById(R.id.tlTableLayout);
        TableRow row = new TableRow(this);
        TextView tvID = new TextView(this);
        TextView tvGoods = new TextView(this);
        TextView tvUnit = new TextView(this);
        TextView tvQuantity = new TextView(this);

        tvID.setText("ID");
        row.addView(tvID);
        tvGoods.setText("Goods");
        row.addView(tvGoods);
        tvUnit.setText("Unit");
        row.addView(tvUnit);
        tvQuantity.setText("Quantity");
        row.addView(tvQuantity);

        tl.addView(row);

        for (Object goodsID : goodsIDs) {
            TableRow tbrow = new TableRow(this);
            TextView tv0 = new TextView(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);

            ArrayList goodsData = mDbHelper.getGoodsByID(goodsID.toString());
            tv0.setText(goodsData.get(0).toString());
            tv1.setText(goodsData.get(1).toString());
            tv2.setText(goodsData.get(2).toString());
            tv3.setText(goodsData.get(3).toString());

            tbrow.addView(tv0);
            tbrow.addView(tv1);
            tbrow.addView(tv2);
            tbrow.addView(tv3);
            tl.addView(tbrow);
        }
    }
    private void populateTnx() {

        TableLayout tl = (TableLayout) findViewById(R.id.tlTableLayout);
        TableRow row = new TableRow(this);
        TextView tvID = new TextView(this);
        TextView tvGoods = new TextView(this);
        TextView tvSeller = new TextView(this);
        TextView tvBuyer = new TextView(this);
        TextView tvQuantity = new TextView(this);
        TextView tvSellerPrice = new TextView(this);
        TextView tvBuyerPrice = new TextView(this);
        TextView tvDesc = new TextView(this);
        TextView tvUnit = new TextView(this);
        TextView tvFOID = new TextView(this);

        tvID.setText("ID ");
        row.addView(tvID);
        tvSeller.setText(" Seller ");
        row.addView(tvSeller);
        tvBuyer.setText(" Buyer ");
        row.addView(tvBuyer);
        tvGoods.setText(" Goods ");
        row.addView(tvGoods);
        tvQuantity.setText(" # ");
        row.addView(tvQuantity);
        tvUnit.setText(" Unit ");
        row.addView(tvUnit);
        tvSellerPrice.setText(" €sell ");
        row.addView(tvSellerPrice);
        tvBuyerPrice.setText(" €buy ");
        row.addView(tvBuyerPrice);
        tvDesc.setText(" Description ");
        row.addView(tvDesc);
        tvFOID.setText(" FOID ");
        row.addView(tvFOID);

        tl.addView(row);

        ArrayList tnxs = mDbHelper.getTransactions();
        for (Object tnx : tnxs) {
            ArrayList a = (ArrayList) tnx; 
            TableRow tbrow = new TableRow(this);
            TextView tv0 = new TextView(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);
            TextView tv4 = new TextView(this);
            TextView tv5 = new TextView(this);
            TextView tv6 = new TextView(this);
            TextView tv7 = new TextView(this);
            TextView tv8 = new TextView(this);
            TextView tv9 = new TextView(this);

            tv0.setText(a.get(0).toString());
            tv1.setText(a.get(1).toString());
            tv2.setText(a.get(2).toString());
            tv3.setText(a.get(3).toString());
            tv4.setText(a.get(4).toString());
            tv5.setText(a.get(5).toString());
            tv6.setText(a.get(6).toString());
            tv7.setText(a.get(7).toString());
            tv8.setText(a.get(8).toString());
            tv9.setText(a.get(9).toString());

            tbrow.addView(tv0);
            tbrow.addView(tv1);
            tbrow.addView(tv2);
            tbrow.addView(tv3);
            tbrow.addView(tv4);
            tbrow.addView(tv5);
            tbrow.addView(tv6);
            tbrow.addView(tv7);
            tbrow.addView(tv8);
            tbrow.addView(tv9);
            tl.addView(tbrow);
        }
    }

}
