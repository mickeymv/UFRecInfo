package ai.api.sample;

/***********************************************************************************************************************
 * API.AI Android SDK -  API.AI libraries usage example
 * =================================================
 * <p>
 * Copyright (C) 2015 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 * <p>
 * **********************************************************************************************************************
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    private static boolean isFirstRequest = true;

    private Gson gson = GsonFactory.getGson();

    ArrayList<FitnessClass> classList = new ArrayList<>();
    ArrayList<FitnessClass> filteredList = new ArrayList<>();

    private final String startMessage = "Hi! "; //Welcome to ‘Talk Fitness To Me’. You can ask to show Group Fitness classes based on day. Which day would you like to see classes for?

    private boolean isThereAChangeInResults = false;

    List<AIContext> contexts = new ArrayList<>();
    AIContext filterContext = new AIContext("filters");
    HashMap filters = new HashMap();
    //static HashMap previousFilters = new HashMap();
    RequestExtras requestExtras;
    /*
        private static Timer timer = new Timer();
        */
    private static String speechForSuggestingFilters = "";

    FitnessClassAdapter fitnessClassListViewAdapter;

    static String[] conditioning = {"Bootcamp", "Stadium Conditioning"};
    static String[] cardio = {"50 50", "Cycle", "Gator Theory", "Interval Training", "Intervals and Yoga", "Kickboxing", "Step", "Zumba Step"};
    static String[] dance = {"Ballet Strength", "Hip Hop Fitness", "Zumba", "Zumba Toning"};
    static String[] yoga = {"Hatha Yoga", "Outdoor Yoga", "Power Yoga", "Recovery Yoga", "Tai Chi", "Vinyasa Yoga", "Yogalates"};
    static String[] strength = {"BOSU", "Core", "iBurn", "Total Body", "ViPR Intervals", "ViPR Total Body"};
    static String[] swimming = {"Aqua Zumba", "Coached Swim"};

    private static TextView emptyText;

    private static boolean hasClearBeenSaid = false;

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
                isFirstRequest = true;
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
        emptyText = (TextView) findViewById(R.id.emptyResultsTextView);
        listView.setEmptyView(emptyText);
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

                    String systemClearOrConfirmationResponse = "";

                    boolean inSystemClearOrConfirmationMode = false;

                    if (!hasClearBeenSaid) { /*Check if the user wants to start over*/
                        JsonElement value = (JsonElement) parameters.get("clear");
                        if (!value.toString().equals("\"\"")) {
                            systemClearOrConfirmationResponse = "Okay, you want to start over? Say Yes or No to confirm.";
                            inSystemClearOrConfirmationMode = true;
                            hasClearBeenSaid = true;
                        }
                    } else { /*Clear was said before, now we look for confirmation from user*/
                        hasClearBeenSaid = false;
                        JsonElement valueE = (JsonElement) parameters.get("confirmation");
                        if (!valueE.toString().equals("\"\"")) {
                            String value = parameters.get("confirmation").toString();
                            value = value.substring(1, value.length() - 1);

                            if (value.equalsIgnoreCase("yes")) {
                                filters.clear();
                                filteredList.clear();
                                Collections.copy(filteredList, classList);
                                fitnessClassListViewAdapter.notifyDataSetChanged();
                                systemClearOrConfirmationResponse = "Okay, let's start over.";
                            } else {
                                systemClearOrConfirmationResponse = "Okay, I won't clear your selections.";
                            }
                            inSystemClearOrConfirmationMode = true;
                        }
                    }
                    if (inSystemClearOrConfirmationMode) {
                        TTS.speak(systemClearOrConfirmationResponse);
                    }

                    if (!inSystemClearOrConfirmationMode) {
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
                                        endChar == '2' && !isFirstRequest) {
                            /*Parameter is from previous context ('#' parameter) */
                                    keyValue = pair.getKey().toString().substring(0, pair.getKey().toString().length() - 1).toLowerCase();
                                    if (keyValue.equals("location")) {
                                        keyValue = "Location";
                                    }
                                    if (copyParameters.get(keyValue).toString().equals("\"\"")) {
                                        filters.put(keyValue, pair.getValue());
                                        //previousFilters.put(keyValue, pair.getValue());
                                    }
                                } else {
                            /*Parameter is from most recent user's utterance ('$' parameter) */
                                    isThereAChangeInResults = true;
                                    if (pair.getKey().toString().toLowerCase().equals("classtype")) {
                                        if (filters.containsKey("class")) {
                                            filters.remove("class");
                                        }
                                        if (parameters.containsKey("class2")) {
                                            parameters.remove("class2");
                                        }
                                    }
                                    filters.put(pair.getKey(), pair.getValue());
                                }
                            }
                            System.out.println(pair.getKey() + " = " + pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException
                        }
                        System.out.println(filters.toString());
                        isFirstRequest = false;
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

                        boolean isDay = false, isLocation = false, isClass = false, isType = false;

                        String day = "", location = "", className = "", type = "";

                        System.out.println(filters);


                        for (FitnessClass fitnessClass : classList) {
                            satisfiesAllFilters = true;
                            Iterator itF = filters.entrySet().iterator();
                            while (itF.hasNext()) {
                                Map.Entry pair = (Map.Entry) itF.next();
                                String filter = (String) pair.getKey();
                                System.out.println("\n\nThe filter key is: " + filter + "\n\n");
                                JsonElement filterV = (JsonElement) pair.getValue();
                                String filterValue = filterV.toString().toLowerCase();
                                filterValue = filterValue.substring(1, filterValue.length() - 1);
                                System.out.println("\n\nThe filter value is: " + filterValue + "\n\n");
                                if (filter.equals("day")) {
                                    if (!(filterValue.toString().toLowerCase().equals(fitnessClass.getDay().toLowerCase()))) {
                                        satisfiesAllFilters = false;
                                    }
                                    isDay = true;
                                    day = filterValue;
                                } else if (filter.equals("class")) {
                                    if (!(filterValue.toString().toLowerCase().equals(fitnessClass.getName().toLowerCase()))) {
                                        satisfiesAllFilters = false;
                                    }
                                    isClass = true;
                                    className = filterValue;
                                } else if (filter.equals("Location")) {
                                    if (!(filterValue.toString().toLowerCase().equals(fitnessClass.getVenue().toLowerCase()))) {
                                        satisfiesAllFilters = false;
                                    }
                                    isLocation = true;
                                    location = filterValue;
                                } else if (filter.equals("classtype")) {
                                    if (!(filterValue.toString().toLowerCase().equals(fitnessClass.getType().toLowerCase()))) {
                                        satisfiesAllFilters = false;
                                    }
                                    isType = true;
                                    type = filterValue;
                                }
                            }
                            if (satisfiesAllFilters) {
                                filteredList.add(fitnessClass);
                            }
                        }

                        int filteredSize = filteredList.size();

                        if (filteredList.isEmpty()) {
                            if (isDay && isClass && isLocation) { //DCL
                                emptyText.setText("\n\nThere are no classes for " + className + " on " + day + " at " + location + ". \n\n");
                            } else if (isDay && !isClass && isLocation && isType) { //DTL
                                emptyText.setText("\n\nThere are no " + type + " classes on " + day + " at " + location + ". \n\n");
                            } else if (isDay && isClass && !isType && !isLocation) { //DC
                                emptyText.setText("\n\nThere are no classes for " + className + " on " + day);
                            } else if (isDay && !isClass && isType && !isLocation) { //DT
                                emptyText.setText("\n\nThere are no classes for " + className + " on " + day);
                            } else if (isDay && !isClass && !isType && isLocation) { //DL
                                emptyText.setText("\n\nThere are no classes on " + day + " at " + location);
                            } else if (!isDay && isClass && !isType && isLocation) { //CL
                                emptyText.setText("\n\nThere are no " + className + " classes at " + location);
                            } else if (!isDay && !isClass && isType && isLocation) { //TL
                                emptyText.setText("\n\nThere are no " + type + " classes at " + location);
                            } else if (isClass && isType && !isLocation && !isDay) {
                                emptyText.setText(className + " is not a type of " + type + "class.");
                            } else if (isClass && isType && !isClassInType(className, type)) {
                                emptyText.setText(className + " is not a type of " + type + " class.");
                            }
                            if (isThereAChangeInResults) {
                                TTS.speak(emptyText.getText().toString());
                                resultTextView.setText(emptyText.getText().toString());
                            }
                        } else if (filteredSize > 5) {

                            String speechLottaResults = result.getFulfillment().getSpeech() + ". I found " + filteredSize + " .";

                            //TTS.speak(speechLottaResults);

                            if (filteredSize >= 10) {
                                speechForSuggestingFilters = "";
                                if (!isDay && isClass && !isLocation) {
                                    speechForSuggestingFilters += "You can filter by day or location.";
                                } else if (isClass && isLocation && !isDay) {
                                    speechForSuggestingFilters += "You can filter by day.\n\n";
                                } else if (isClass && isDay && !isLocation) {
                                    speechForSuggestingFilters += "You can filter by location.\n\n";
                                } else if (isType && !isClass && !isDay && !isLocation) {
                                    speechForSuggestingFilters += "You can filter by class, day, or location.\n\n";
                                } else if (isType && !isClass && !isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class or day.\n\n";
                                } else if (isType && !isClass && !isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class or day.\n\n";
                                } else if (isType && !isClass && isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class.\n\n";
                                } else if (!isType && !isClass && isDay && !isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class name, class type, or location.\n\n";
                                } else if (!isType && !isClass && isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class name or class type.\n\n";
                                } else if (isType && !isClass && isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class.\n\n";
                                } else if (!isType && !isClass && !isDay && isLocation) {
                                    speechForSuggestingFilters += "\n\nYou can filter by class name, class type, or day.\n\n";
                                }
/*
                                          timer.schedule(new TimerTask() {
                                              @Override
                                              public void run() {
                                                  TTS.delayedSpeak(speechForSuggestingFilters);
                                              }
                                          }, 10*1000);
                                      }
*/
                            }
                            if (isThereAChangeInResults) {
                                TTS.speak(speechLottaResults + speechForSuggestingFilters);
                                resultTextView.setText(speechLottaResults + speechForSuggestingFilters);
                            }

                        } else { /*size of list between 1 and 5 results*/
                            if (isThereAChangeInResults) {
                                TTS.speak(result.getFulfillment().getSpeech());
                                resultTextView.setText(result.getFulfillment().getSpeech());
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
                    if (metadata != null)

                    {
                        Log.i(TAG, "Intent id: " + metadata.getIntentId());
                        Log.i(TAG, "Intent name: " + metadata.getIntentName());
                    }

                    final HashMap<String, JsonElement> params = result.getParameters();
                    if (params != null && !params.isEmpty())

                    {
                        Log.i(TAG, "Parameters: ");
                        for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                            Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                        }
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
        /*
        timer.cancel();
        timer.purge();
        */
        if (TTS.textToSpeech.isSpeaking()) {
            TTS.textToSpeech.stop();
        }
        aiDialog.showAndListen(requestExtras);
    }

    public static boolean isClassInType(String className, String type) {
        if (getClassType(className).toLowerCase().equals(type.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    public static String getClassType(String a) {

        for (int i = 0; i < conditioning.length; i++) {
            if (a.toLowerCase().equals(conditioning[i].toLowerCase()))
                return "Conditioning";
        }
        for (int i = 0; i < cardio.length; i++) {
            if (a.toLowerCase().equals(cardio[i].toLowerCase()))
                return "Cardio";
        }
        for (int i = 0; i < dance.length; i++) {
            if (a.toLowerCase().equals(dance[i].toLowerCase()))
                return "Dance";
        }
        for (int i = 0; i < yoga.length; i++) {
            if (a.toLowerCase().equals(yoga[i].toLowerCase()))
                return "Yoga";
        }
        for (int i = 0; i < strength.length; i++) {
            if (a.toLowerCase().equals(strength[i].toLowerCase()))
                return "Strength";
        }
        for (int i = 0; i < swimming.length; i++) {
            if (a.toLowerCase().equals(swimming[i].toLowerCase()))
                return "Swimming";
        }
        return "";
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
            classList = gson.fromJson(json, new TypeToken<List<FitnessClass>>() {
            }.getType());
            filteredList.addAll(classList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
