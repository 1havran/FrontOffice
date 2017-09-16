package sk.hppa.frontoffice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddGoods extends AppCompatActivity {
    final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(AddGoods.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);


        final EditText eGoods = (EditText) findViewById(R.id.edtGoods);
        final EditText eQuantity = (EditText) findViewById(R.id.edtQuantity1);
        final EditText eUnit = (EditText) findViewById(R.id.edtUnit);
        final Spinner spinnerGoods = (Spinner) findViewById(R.id.spinnerGoods1);
        final Button btnSend = (Button) findViewById(R.id.btnAddGoods);

        ArrayAdapter<String> adapterGoods = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getGoods());
        spinnerGoods.setAdapter(adapterGoods);
        spinnerGoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String GoodsID = spinnerGoods.getSelectedItem().toString().split(" ")[0];
                    String GoodsLabel = spinnerGoods.getSelectedItem().toString().split(" ")[1];
                    Double GoodsQuantity = mDbHelper.getAvailableGoodsQuantity(GoodsID);
                    String GoodsUnit = mDbHelper.getAvailableGoodsUnit(GoodsID);
                    eGoods.setText(GoodsLabel);
                    eQuantity.setText(GoodsQuantity.toString());
                    eUnit.setText(GoodsUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    ArrayList al = mDbHelper.getGoodsIDsByName(eGoods.getText().toString());
                    Double d = Double.parseDouble(eQuantity.getText().toString());
                    if (al.size() > 0) {
                        mDbHelper.updateQuantityOfGoods(al.get(0).toString(), d);
                    } else {
                        String goodsFOID = "DD" + System.currentTimeMillis();
                        mDbHelper.insertGoods(eGoods.getText().toString(), goodsFOID, d, eUnit.getText().toString());
                    }
                    Toast.makeText(AddGoods.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(AddGoods.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
