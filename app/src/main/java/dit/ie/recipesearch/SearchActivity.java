package dit.ie.recipesearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import dit.ie.recipesearch.utilities.ApiQuery;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    // Search box for the query
    private EditText searchBox;

    // Shows the query URL
    //private TextView UrlDisplayTextView;

    // Display json results
    private TextView resultsDisplay;


    // Search button
    private Button searchButton;

    // array list of recipes
    ArrayList<HashMap<String, String>> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Variables for layout
        searchBox = (EditText) findViewById(R.id.searchBox);
        resultsDisplay = (TextView) findViewById(R.id.resultsDisplay);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeSearchQuery();
            }

        });
    }

    // Retries text value from search box
    private void makeSearchQuery() {
        String searchQuery = searchBox.getText().toString();
        URL searchUrl = ApiQuery.buildUrl(searchQuery);
        new searchQueryTask().execute(searchUrl);
    }

    // Create network thread for the process of querying the API
    public class searchQueryTask extends AsyncTask<URL, Void, String> {
        // Background task for querying API. Catches error if invalid data is retrieved
        @Override
        protected String doInBackground(URL... params) {
            URL searchURL = params[0];
            String searchResults = null;
            try {
                searchResults = ApiQuery.getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        // Once preExecute is finished, the postExecute thread runs
        @Override
        protected void onPostExecute(String searchResults) {
            // If results come back then set the textView to display them

            if (searchResults != null) {
                recipeList = new ArrayList<HashMap<String, String>>();
                try {
                    JSONObject jsonObject = new JSONObject(searchResults);
                    // getting JSON array code
                    JSONArray recipes = jsonObject.getJSONArray("hits");

                    // looping through all recipes
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject c = recipes.getJSONObject(i);

                        JSONObject recipe = c.getJSONObject("recipe");
                        String label = recipe.getString("label");
                        String image = recipe.getString("image");

                        // serving
                        String yield = "Serving size: " + Integer.toString(recipe.getInt("yield"));

                        String source = recipe.getString("source");
                        String url = recipe.getString("url");
                        String calories = "Calories: " + recipe.getString("calories").substring(0,4);
                        String totalWeight = recipe.getString("totalWeight");

                        JSONArray ingredientLines = recipe.getJSONArray("ingredientLines");
                        String ingredients[] = new String[ingredientLines.length()];
                        // Convert the JSONArray of ingredients to a normal string array
                        for (int j = 0; j < ingredientLines.length(); j++) {
                            ingredients[j] = (ingredientLines.get(j).toString());
                        }

                        JSONObject totalNutrients = recipe.getJSONObject("totalNutrients");
                        JSONObject totalDaily = recipe.getJSONObject("totalDaily");

                        HashMap<String, String> recipeOutput = new HashMap<>();

                        // adding each child node to hash map
                        recipeOutput.put("label", label);
                        recipeOutput.put("image", image);
                        recipeOutput.put("yield", yield);
                        recipeOutput.put("source", source);
                        recipeOutput.put("url", url);
                        recipeOutput.put("calories", calories);
                        recipeOutput.put("totalWeight", totalWeight);
                        for (int k = 0; k < ingredients.length; k++) {
                            recipeOutput.put("ingredients", ingredients[k]);
                        }


                        recipeList.add(recipeOutput);
                    }
                }  catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                ListView list= (ListView) findViewById(R.id.listView);
                ListAdapter adapter =
                        new MyAdapter(
                                getBaseContext(),
                                recipeList,
                                R.layout.recipe_list,
                                new String[]{"label","image","yield","calories"},
                                new int[]{R.id.label,R.id.image,R.id.yield,R.id.calories});
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> selected = recipeList.get(position);
                        // pass Hashmap using intent
                        Intent intent = new Intent(SearchActivity.this, DisplayActivity.class);
                        intent.putExtra("recipe", selected);
                        startActivity(intent);
                    }
                });
            // Else print a message saying there were none found
            } else {
                resultsDisplay.setText("No results found.");
            }

        }

    }

    public class MyAdapter extends SimpleAdapter{

        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to){
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            // built SimpleAdapter
            View v = super.getView(position, convertView, parent);

            // Get reference for Picasso
            ImageView img = (ImageView) v.getTag();
            if(img == null){
                img = (ImageView) v.findViewById(R.id.recipeImage);
                v.setTag(img);
            }
            // get the url from the data you passed
            String url = recipeList.get(position).get("image");
            // do Picasso
            Picasso.get().load(url).into(img);

            // return the view
            return v;
        }
    }
}
