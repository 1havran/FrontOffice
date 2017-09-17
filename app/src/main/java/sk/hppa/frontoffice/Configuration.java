package sk.hppa.frontoffice;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Activity {

    String emailRecipient = "", FOID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        final EditText eEmail = (EditText) findViewById(R.id.edtEmail);
        final EditText eFoid = (EditText) findViewById(R.id.edtFOID);
        final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(Configuration.this);


        emailRecipient = mDbHelper.getMetadataByKey("emailRecipient");
        FOID = mDbHelper.getMetadataByKey("FOID");

        eEmail.setText(emailRecipient);
        eFoid.setText(FOID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailRecipient = eEmail.getText().toString();
                FOID = eFoid.getText().toString();
                mDbHelper.updateMetadataByKey("emailRecipient", emailRecipient);
                mDbHelper.updateMetadataByKey("FOID", FOID);
                Toast.makeText(Configuration.this, "OK", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
