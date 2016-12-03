package ai.api.sample;

/***********************************************************************************************************************
 *
 * API.AI Android SDK -  API.AI libraries usage example
 * =================================================
 *
 * Copyright (C) 2015 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.GsonFactory;
import ai.api.RequestExtras;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIOutputContext;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIDialog;

public class AIDialogSampleActivity extends BaseActivity implements AIDialog.AIDialogListener {

    private static final String TAG = AIDialogSampleActivity.class.getName();

    private TextView resultTextView;
    private AIDialog aiDialog;

    private Button startButton;
    private Button speakButton;

    private Gson gson = GsonFactory.getGson();

    ArrayList<FitnessClass> classList = new ArrayList<>();
    ArrayList<FitnessClass> filteredList = new ArrayList<>();

    private final String startMessage = "Hi! "; //Welcome to ‘Talk Fitness To Me’. You can ask to show Group Fitness classes based on day. Which day would you like to see classes for?

    List<AIContext> contexts = new ArrayList<>();
    AIContext filterContext = new AIContext("filters");
    HashMap filters = new HashMap();
    //static HashMap previousFilters = new HashMap();
    RequestExtras requestExtras;

    FitnessClassAdapter fitnessClassListViewAdapter;

    {
        filterContext.setParameters(filters);
        contexts.add(filterContext);
        requestExtras = new RequestExtras(contexts, null);
    }

    ListView listView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidialog_sample);

        resultTextView = (TextView) findViewById(R.id.resultTextView);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDialog = new AIDialog(this, config);
        aiDialog.setResultsListener(this);

        startButton = (Button) findViewById(R.id.buttonStart);
        speakButton = (Button) findViewById(R.id.buttonListen);
        speakButton.setVisibility(Button.INVISIBLE);
        speakButton.setEnabled(false);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filters.clear(); //Clear the context object of previous filters.
                //previousFilters.clear();
                startButton.setEnabled(false);
                startButton.setVisibility(Button.INVISIBLE);
                resultTextView.setEnabled(true);
                resultTextView.setVisibility(Button.VISIBLE);
                resultTextView.setText(startMessage);
                TTS.speak(startMessage);

                while (TTS.textToSpeech.isSpeaking()) {
                }
                speakButton.setVisibility(Button.VISIBLE);
                speakButton.setEnabled(true);
            }
        });
        loadJSONFromAsset();

        listView = (ListView) this.findViewById(R.id.classListView);
        fitnessClassListViewAdapter = new FitnessClassAdapter(this, filteredList);
        listView.setAdapter(fitnessClassListViewAdapter);
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
    }
*/
    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "onResult");



                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                Log.i(TAG, "Action: " + result.getAction());
                String speech = resultTextView.getText().toString();
                /*
                speech += "\n\nAgent: " + result.getFulfillment().getSpeech();
                */
                Log.i(TAG, "Speech: " + speech);

                resultTextView.setText(speech);
                TTS.speak(result.getFulfillment().getSpeech());

                AIOutputContext filterContext = response.getResult().getContext("filters");

                /*
                * TODO: Switch here to check if the response is for classes or from the help intent
                * */
if (filterContext != null) {
    Map parameters = filterContext.getParameters();

    filters.clear(); //Clear the context object of previous filters.
    //previousFilters.clear();


    System.out.println(parameters.toString());
    Iterator it = parameters.entrySet().iterator();

    HashMap copyParameters = new HashMap(parameters);

    String keyValue;
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry) it.next();
        JsonElement value = (JsonElement) pair.getValue();
        if (!value.toString().equals("\"\"") && !pair.getKey().toString().contains(".original")) {
            char endChar = pair.getKey().toString().charAt(pair.getKey().toString().length() - 1);
            if (
                            /*Set next new context sent with the request with the parameters
                                receieved from the last API.AI response
                                 */
                    endChar == '2') {
                            /*Parameter is from previous context ('#' parameter) */

                keyValue = pair.getKey().toString().substring(0, pair.getKey().toString().length() - 1).toLowerCase();
                if(keyValue.equals("location")) {
                    keyValue = "Location";
                }
                if (copyParameters.get(keyValue).toString().equals("\"\"")) {
                    filters.put(keyValue, pair.getValue());
                    //previousFilters.put(keyValue, pair.getValue());
                }
            } else {
                            /*Parameter is from most recent user's utterance ('$' parameter) */
                filters.put(pair.getKey(), pair.getValue());
            }
        }
        System.out.println(pair.getKey() + " = " + pair.getValue());
        it.remove(); // avoids a ConcurrentModificationException
    }
    System.out.println(filters.toString());

                                /*
                day-> Day
                class-> Name
                 */



                                /* For 1. day, replace today, tomorrow, weekend, etc.
                        2. date, replace date
                        with the correct day of the week.
                * */

    filteredList.clear();

    boolean satisfiesAllFilters;
    for (FitnessClass fitnessClass : classList) {
        satisfiesAllFilters = true;
        it = filters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String filter = (String) pair.getKey();
            JsonElement filterV = (JsonElement) pair.getValue();
            String filterValue = filterV.toString().toLowerCase();
            if (filter.equals("day")) {
                if (!(filterValue.toString().contains(fitnessClass.getDay().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("class")) {
                if (!(filterValue.toString().contains(fitnessClass.getName().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("date")) {
                            /*Convert date into what specific day*/
                            /*
                            if (!(filterValue.toString().contains(fitnessClass.getName()))) {
                                satisfiesAllFilters = false;
                                break;
                            }
                            */
            } else if (filter.equals("time")) {
                            /*Convert time into a format recognized by us*/
                            /*
                            if (!(filterValue.toString().contains(fitnessClass.getName()))) {
                                satisfiesAllFilters = false;
                                break;
                            }
                            */
            } else if (filter.equals("time-period")) {
                            /*Convert time-period into a format recognized by us*/
                            /*
                            if (!(filterValue.toString().contains(fitnessClass.getName()))) {
                                satisfiesAllFilters = false;
                                break;
                            }
                            */
            } else if (filter.equals("type3")) {
                if (!(filterValue.toString().contains(fitnessClass.getType_2().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("duration")) {
                if (!(filterValue.toString().contains(fitnessClass.getDuration().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("Location")) {
                if (!(filterValue.toString().contains(fitnessClass.getVenue().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("classtype")) {
                if (!(filterValue.toString().contains(fitnessClass.getType().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            } else if (filter.equals("time-of-day")) {
                            /*Convert morning etc. to select the times.*/
                            /*
                            if (!(filterValue.toString().contains(fitnessClass.getVenue()))) {
                                satisfiesAllFilters = false;
                                break;
                            }
                            */
            } else if (filter.equals("Instructor")) {
                if (!(filterValue.toString().contains(fitnessClass.getInstructor().toLowerCase()))) {
                    satisfiesAllFilters = false;
                    break;
                }
            }
        }
        if (satisfiesAllFilters) {
            filteredList.add(fitnessClass);
        }
    }

                /*
                Arrays.fill(filteredFitnessClasses,null);
                fitnessClassListViewAdapter.notifyDataSetChanged();
*/

    //listView.setAdapter(fitnessClassListViewAdapter);
    fitnessClassListViewAdapter.notifyDataSetChanged();
    //listView.invalidateViews();
    //listView.refreshDrawableState();

                                /*
                * TODO: Switch here to show classes or help intent
                * */

    //resultTextView.setText(filteredList.toString());
}
                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
            }

        });
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*
                * */
                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
        if (aiDialog != null) {
            aiDialog.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (aiDialog != null) {
            aiDialog.resume();
        }
        super.onResume();
    }

    public void buttonListenOnClick(final View view) {
        /*  Send every request with a context.
        */

        aiDialog.showAndListen(requestExtras);
    }

    public void loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("classes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            classList = gson.fromJson(json, new TypeToken<List<FitnessClass>>(){}.getType());
            filteredList.addAll(classList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
