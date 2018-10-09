package dit.ie.recipesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DisplayActivity extends AppCompatActivity {

    // Text for displaying recipe name
    private TextView recipeName;
    private TextView calories;
    private TextView url;
    private TextView ingredients;
    private TextView yield;
    private ImageView image;

    private Button goBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        HashMap<String, String> recipe = (HashMap<String, String>) getIntent().getSerializableExtra("recipe");

        image = findViewById(R.id.recipeImage);
        String imgUrl = recipe.get("image");
        Picasso.get().load(imgUrl).into(image);

        recipeName = findViewById(R.id.recipeName);
        recipeName.setText(recipe.get("label"));

        url = findViewById(R.id.url);
        url.setText(recipe.get("url"));

        calories = findViewById(R.id.calories);
        calories.setText(recipe.get("calories"));

        ingredients = findViewById(R.id.ingredients);
        ingredients.setText(recipe.get("ingredients"));

        yield = findViewById(R.id.weight);
        yield.setText(recipe.get("yield"));



        goBack = findViewById(R.id.back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
