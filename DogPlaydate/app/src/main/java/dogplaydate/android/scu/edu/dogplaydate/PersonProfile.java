package dogplaydate.android.scu.edu.dogplaydate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

/**
 * Created by D on 2015/5/20.
 */
public class PersonProfile extends Activity{
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_file_layout);
        button = (Button)findViewById(R.id.button4);
        button.setOnClickListener(myhandler1);
    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent staticActivityIntent = new Intent(PersonProfile.this, DogProfile.class);
            startActivity(staticActivityIntent);
        }
    };





}
