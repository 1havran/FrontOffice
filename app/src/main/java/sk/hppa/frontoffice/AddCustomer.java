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

public class AddCustomer extends AppCompatActivity {
    final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(AddCustomer.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        final EditText eCustomer = (EditText) findViewById(R.id.edtCustomer);
        final Spinner spinnerCustomer = (Spinner) findViewById(R.id.spinnerCustomers);
        final Button btnSend = (Button) findViewById(R.id.btnAddCustomer);

        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getCustomers());
        spinnerCustomer.setAdapter(adapterCustomer);
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String CustomerLabel = spinnerCustomer.getSelectedItem().toString().split(" ")[1];
                eCustomer.setText(CustomerLabel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    ArrayList al = mDbHelper.getCustomerIDsByName(eCustomer.getText().toString());
                    if (al.size() > 0) {
                        String custID = spinnerCustomer.getSelectedItem().toString().split(" ")[0];
                        mDbHelper.updateCustomerByID(custID, eCustomer.getText().toString(), null);
                    } else {
                        String custFOID = "DD" + System.currentTimeMillis();
                        mDbHelper.insertCustomer(eCustomer.getText().toString(), custFOID, null);
                    }
                    Toast.makeText(AddCustomer.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(AddCustomer.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }
}
