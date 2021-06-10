package ui.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.shinydisco.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private AuthFragmentAdapter authFragmentAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // setup auth (sign in and sign up) tabs
        authFragmentAdapter = new AuthFragmentAdapter(this);
        viewPager2 = findViewById(R.id.auth_elements_pager);
        viewPager2.setAdapter(authFragmentAdapter);
        TabLayout tabLayout = findViewById(R.id.auth_tabs);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 1)
                        tab.setText(R.string.sign_up);
                    else
                        tab.setText(R.string.sign_in);
                }
        ).attach();
    }


}