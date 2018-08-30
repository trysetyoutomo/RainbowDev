package id.co.ale.rainbowDev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ale.rainbowsdk.RainbowSdk;

import id.co.ale.rainbowDev.R;

public class FrontPageActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnPromotion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        this.btnLogin = (Button) findViewById(R.id.buttonLoginFront);
        this.btnPromotion = (Button) findViewById(R.id.buttonPromotionFront);
        this.btnLogin.setOnClickListener(goToLoginPage);
        this.btnPromotion.setOnClickListener(goToPromotionPage);
    }

    private View.OnClickListener goToLoginPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(FrontPageActivity.this,LoginActivity.class);
            startActivityForResult(intent,69);

        }
    };
    private View.OnClickListener goToPromotionPage = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(FrontPageActivity.this, PromotionActivity.class);
            startActivityForResult(intent, 69);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 69 && resultCode == RESULT_OK)
        {
            Intent intent = new Intent(RainbowSdk.instance().getContext(), ImsgActivity.class);
            startActivity(intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
