package com.bethuneci.vmc;

// For generating random operator and operands in Speed Arithmetics
import java.util.Random;
// For creating dialog in the app.
import android.app.AlertDialog;
import android.content.DialogInterface;
// For saving persistent data that is created by the app.
import android.content.SharedPreferences;
import android.os.Bundle;
// For count down timer in Speed Arithmetics Section.
import android.os.CountDownTimer;
// For supporting Fragment transactions
import java.util.Locale;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
// Log object for creating error messages in LogCat.
import android.util.Log;
// LayoutInflater for inflating layouts.
import android.view.LayoutInflater;
// For creating menu on top-right.
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
// Widgets, Views and listener used by the program
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
// For accessing assets folder
import android.content.res.AssetManager;
// For reading question and answers text file in assets folder.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// For accessing, reading, and setting images for ImageView.
import java.io.InputStream;
import android.graphics.drawable.Drawable;
// For loading and playing sounds effects
import android.media.SoundPool;
import android.media.AudioManager;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/*
	 * The android.support.v4.view.PagerAdapter that will provide
	 * fragments for each of the sections. We use a
	 * android.support.v4.app.FragmentPagerAdapter derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * android.support.v4.app.FragmentStatePagerAdapter.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/*
	 * The {ViewPager that will host the section contents.
	 */
	ViewPager mViewPager;
	
	// Set the file name for stored data in SharedPreferances
	private static final String SAVED_DATA_NAME = "VirtualMathContestSettings";
	// static final variable for grade level 1 to 2.
	private static final int GRADE1TO2 = 0;
	// static final variable for grade level 3 to 4.
	private static final int GRADE3TO4 = 1;
	// static final variable for grade level 5 to 6.
	private static final int GRADE5TO6 = 2;
	
	private static int gradeLevel;
	private static int questionSolved;
	private static int pointsEarned;
	
	private static String[] gradeOptions;
	// AssetManager for managing external resources
	private static AssetManager assets;
	
	// Random number generator for the program
	private static Random randGenerator;
	
	// References to three fragments in the app.
	private static MainMenuFragment mainMenu;
	private static TechniquesTrainerFragment techniquesTrainer;
	private static SpeedArithmeticFragment speedArithmetic;
	
	// SoundPool object for play sounds.
	private static SoundPool soundPool;
	// id for sound "right.wav"
	private static int correctSoundId;
	// id for sound "wrong.wav"
	private static int incorrectSoundId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// Allow using volume button to change the volume of sound effects.
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// create A SoundPool object
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		// put the two sounds in SoundPool object we created.
		correctSoundId = soundPool.load(this, R.raw.right, 1);
		incorrectSoundId = soundPool.load(this, R.raw.wrong, 1);
		
		// Initialize AssetManager instance
		assets = getAssets();
		
		// Initialize gradeOptions array
		gradeOptions = getResources().getStringArray(R.array.grades);
		
		// Restore preference and saved data. If the requested data is not
		// found in SharedPreferences, preference will be the default and saved data will be cleared.
		SharedPreferences settingAndSavedData = getSharedPreferences(SAVED_DATA_NAME,0);
		gradeLevel = settingAndSavedData.getInt("gradeLevel",GRADE1TO2);
		questionSolved = settingAndSavedData.getInt("questionSolved",0);
		pointsEarned = settingAndSavedData.getInt("pointsEarned", 0);
		
		// Initialize Random instance
		randGenerator = new Random();
		
		// Initialize three fragments in the app.
		mainMenu = new MainMenuFragment();
		techniquesTrainer = new TechniquesTrainerFragment();
		speedArithmetic = new SpeedArithmeticFragment();

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		
		// Instantiate an Editor object to change the data to be saved.
		SharedPreferences settingAndSavedData = getSharedPreferences(SAVED_DATA_NAME,0);
		SharedPreferences.Editor editor = settingAndSavedData.edit();
		editor.putInt("gradeLevel",gradeLevel);
		editor.putInt("questionSolved",questionSolved);
		editor.putInt("pointsEarned", pointsEarned);
		
		// commit the edits.
		editor.commit();
	}
	// Method invoked when a tab is selected. 
	@Override
	public void onTabSelected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}
	
	// Method invoked when a tab is unselected. Required by ActionBar.TabListener.
	@Override
	public void onTabUnselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
	}
	// Method invoked when a tab is reselected. Required by ActionBar.TabListener.
	@Override
	public void onTabReselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
	}
	
	// A FragmentPagerAdapter that returns a fragment corresponding to
	// one of the sections/tabs/pages.
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		// getItem is called to instantiate the fragment for the given page.
		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			if (position == 0){
				fragment = mainMenu;
			}
			else if (position == 1){
				fragment = techniquesTrainer;
			}
			else{
				fragment = speedArithmetic;
			}
			return fragment;
		}
		
		// return the number of total pages.
		@Override
		public int getCount() {
			return 3;
		}

		// This method returns the page title of each tab
		// It takes one parameter, the index of the position of the tab.
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

    // Class representing the main menu fragment. 
	// It is required by Android API that an inner subclass of Fragment is public and static.
    public static class MainMenuFragment extends Fragment {
    	private TextView gradeLevelTextView;
    	private TextView questionSolvedTextView;
    	private TextView pointTextView;
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    		// get the view of this fragment from xml layout file.
    		View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
    		return rootView;
    	}
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState){
    		super.onCreate(savedInstanceState);
    		// tell the fragment that there is an options menu on top right.
    		setHasOptionsMenu(true);
    	}
    	
    	
    	// This method is being called after its activity has completed its own onCreate() method.
    	@Override
    	public void onActivityCreated(Bundle savedInstanceState){
    		super.onActivityCreated(savedInstanceState);
    		// get the corresponding TextView in the layout file
    		gradeLevelTextView = (TextView) getActivity().findViewById(R.id.gradeLevelTextView);
    		questionSolvedTextView = (TextView) getActivity().findViewById(R.id.questionSolvedTextView);
    		pointTextView = (TextView) getActivity().findViewById(R.id.pointTextView);
    		
    		refreshStatisticsTextView();
    		
    	}
    	
    	// called automatically for creating a menu on top-right
    	@Override
    	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    		super.onCreateOptionsMenu(menu,inflater);
    		// add "Grade Level" option as the first item
    		menu.add(Menu.NONE,Menu.FIRST,Menu.NONE,R.string.gradeLevelMenuText);
    		// add "Reset Statistics" option as the second item
    		menu.add(Menu.NONE,Menu.FIRST + 1,Menu.NONE,R.string.resetText);
    	}
    	
    	// called automatically when an option in the settings menu is pressed.
    	// returns true if any item is selected, false otherwise.
    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    		// Create an AlertDialog Builder for creating dialogs
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		// If the user pressed "Grade Level" option
    		if (item.getItemId() == Menu.FIRST){
    			builder.setTitle(R.string.selectGradeLevelText);
    			
    			// add array of possible choices to the dialog and set the event handler when one option is tapped.
    			builder.setItems(R.array.grades, new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					gradeLevel = which;
    	    			refreshStatisticsTextView();
    				}
    			});
    			builder.show();
    			return true;
    		}
    		// if the user pressed "Reset Statistics" option
    		else if (item.getItemId() == Menu.FIRST + 1){
    			builder.setTitle(R.string.areYouSureText);
    			builder.setMessage(R.string.eraseText);
    			// Create a positive choice showing "Confirm" on the dialog with a abstract class for event handling
    			builder.setPositiveButton(R.string.confirmText, new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					questionSolved = 0;
    					pointsEarned = 0;
    					refreshStatisticsTextView();
    				}
    			});
    			// create a negative choice showing "Cancel" on the dialog without event handling (do nothing if cancel is pressed.)
    			builder.setNegativeButton(R.string.cancelText, null);
    			builder.show();
    			return true;
    		}
    		return false;
    	}
    	
    	// this private method refreshes the content of gradeLevelTextView,questionSolvedTextView and pointTextView.
    	private void refreshStatisticsTextView(){
    		// change gradeLevelTextView to show the grade level the user is at.
    		gradeLevelTextView.setText(getString(R.string.gradeLevelText) + " " + gradeOptions[gradeLevel]);
    		// change questionSolvedTextView so that it shows the number of questions answered
    		questionSolvedTextView.setText(getString(R.string.questionSolvedText) + " " + questionSolved + " " + getString(R.string.question) + ((questionSolved > 1) ? "s" : ""));
    		// change pointTextView so it shows how many points are earned
    		pointTextView.setText(getString(R.string.pointText) + " " + pointsEarned + " " + getString(R.string.point) + ((pointsEarned > 1) ? "s" : "") + ".");
    	}
    }
    
    // a generic fragment for Techniques Trainer and Speed Arithmetics.
    // It is required by Android API that an inner subclass of Fragment is public and static.
    public static class QuestionFragment extends Fragment{
    	protected TextView questionInfoTextView;
    	protected TextView questionTextView;
    	protected Button submitButton;
    	protected EditText answerEditText;
    	protected ImageView resultImageView;
    	protected TextView resultTextView;
    	
    	private String answer;
    	// the amount of points the user is going to get if they get it right
    	private int points;
    	// boolean variable indicating if the user get the question right.
    	private static boolean isCorrect;	
    	
    	// this method sets the question and change appropriate Views in order to show them in 
    	// the appropriate Fragment.
    	// It takes four parameter: String to be set to QuestionInfoTextView, String to be set
    	// to the QuestionTextView, String that is the answer to the question, and an integer for
    	// the point value of this question.
    	public void setQuestionInfo(String information,String question,String answer,int points){
    		setQuestionsViewVisibilty(View.VISIBLE);
    		setResultsViewVisibilty(View.INVISIBLE);
    		
    		questionInfoTextView.setText(information);
    		questionTextView.setText(question);
    		
    		this.answer = answer;
    		this.points = points;
    		
    		isCorrect = false;
			answerEditText.setEnabled(true);
			submitButton.setEnabled(true);
    	}
    	
    	// public method that sets the visibility of questionTextView,
    	// answerEditText and submitButton altogether
    	public void setQuestionsViewVisibilty(int state){
    		questionTextView.setVisibility(state);
			answerEditText.setVisibility(state);
			submitButton.setVisibility(state);
    	}
    	
    	// public method that sets the visibility of resultTextView,
    	// and resultImageView altogether
    	public void setResultsViewVisibilty(int state){
			resultTextView.setVisibility(state);
			resultImageView.setVisibility(state);
    	}
    	
    	// a accessory method for isCorrect
    	public boolean getIsCorrect(){
    		return isCorrect;
    	}
    	
    	// Private helper method to change resultImageView according to
    	// if the user gets the answer correct or not.
    	private void setResultImage(){
    		String filename = (isCorrect) ? "wellDone.png" : "tryAgain.png";
    		try{
    			InputStream stream = assets.open("images/" + filename);
    			Drawable image = Drawable.createFromStream(stream, filename);
    			resultImageView.setImageDrawable(image);
    		}
    		catch(IOException iOException){
    			Log.e("Virtual Math Contest", "Error Loading " + filename , iOException);
    		}
    	}
    	
    	// public method for deliberately make the user wrong.  
    	// One example use is when the user is out of time in Speed arithmetics.
    	public void makeWrong(){
    		isCorrect = false;
    		setResultImage();
    		// play the sound effect for incorrect answer
			soundPool.play(incorrectSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    		resultTextView.setText(R.string.tryAgainText);
    	}
    	
    	// listener class for submitButton
    	protected class SubmitButtonListener implements OnClickListener{
    		@Override
    		public void onClick(View v){
    			try{
    				// if the user is right
    				if (answer.equals(answerEditText.getText().toString())){
    					isCorrect = true;
    					setResultImage();
    					resultTextView.setText(getString(R.string.correctText) + " " + 
    							points + " " + getString(R.string.point) + ((points > 1) ? "s" : "") + ".");
    					
    					// disable answerEditText and submitButton so that user will 
    					// not enter the right answer twice for earning 2 same points for questions.
    					answerEditText.setEnabled(false);
    					submitButton.setEnabled(false);
    					// play the sound effect for correct answer
    					soundPool.play(correctSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    					questionSolved++;
    					pointsEarned += points;
    					mainMenu.refreshStatisticsTextView();
    				}
    				else{
    					// if user is wrong
    					makeWrong();
    				}
    				// make resultTextView and resultImageView visible
    				setResultsViewVisibilty(View.VISIBLE);
    			}
    			// if answer is not passed in (or question is not passed in)
    			catch(NullPointerException nullPointerException){
    				questionInfoTextView.setText(R.string.questionNotPickedText);
    			}
    			// clear text in answerEditText.
    			answerEditText.setText("");
    		}
    	}
    }
    
    // Class representing Techniques Trainer fragment.
    // It is required by Android API that an inner subclass of Fragment is public and static.
    public static class TechniquesTrainerFragment extends QuestionFragment{
    	private Button pickQuestionButton;
    	private Button randomQuestionButton;
    	private String[] topics;
    	private String[][] questions;
    	private String[][] answers;
		private int numTopic;
		private int numQuestion;
		
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    		// get the view of this fragment from xml layout file.
    		View rootView = inflater.inflate(R.layout.fragment_techniques_trainer, container, false);
    		return rootView;
    	}
    	
    	public void onActivityCreated(Bundle savedInstanceState){
    		super.onActivityCreated(savedInstanceState);
    		// make instance variables in superclass reference to the View in fragment_techniques_trainer.xml 
    		questionInfoTextView = (TextView) getActivity().findViewById(R.id.questionInfoTextView);
    		questionTextView = (TextView) getActivity().findViewById(R.id.questionTextView);
    		submitButton = (Button)getActivity().findViewById(R.id.submitButton);
    		answerEditText = (EditText) getActivity().findViewById(R.id.answerEditText);
    		resultImageView = (ImageView) getActivity().findViewById(R.id.resultImageView);
    		resultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    		
    		// register submitButton to SubmitButtonListener
    		submitButton.setOnClickListener(new SubmitButtonListener());

    		pickQuestionButton = (Button) getActivity().findViewById(R.id.pickQuestionButton);
    		pickQuestionButton.setOnClickListener(new PickQuestionButtonListener());
    		
    		randomQuestionButton = (Button) getActivity().findViewById(R.id.randomQuestionButton);
    		randomQuestionButton.setOnClickListener(new RandomQuestionButtonListener());
    		
    	}
    	// private helper method for loading questions and answers
    	private void loadQuestionsAndAnswers(){
    		// Initialize an BufferedReader reference for reading contents in the file.
    		BufferedReader fileReader;
    		String currentQuestion = "";
    		try{
    			// find the topics (The name of the sub folders in according grade level)
    			topics = assets.list("questions/"+gradeLevel);
    			questions = new String[topics.length][];
    			answers = new String[topics.length][];
    			// get the questions in the folder
    			for ( int i = 0 ; i < topics.length ; i++){
    				// one question contains 2 files: "qX.txt" and "aX.txt"
    				// "X" is the question number
    				int numQuestionsInCategory = (assets.list("questions/"+gradeLevel+"/"+topics[i]).length/2);
    				questions[i]= new String[numQuestionsInCategory];
    				answers[i]= new String[numQuestionsInCategory];
    				
    				for (int j = 0; j < numQuestionsInCategory; j++){
    					String temp = "";
    					// reading the question
    					// create A new BufferedReader object from InputStreamReader object, which is returned by
    					// InputStream object from AssetMeneger's open() method
    					currentQuestion = "questions/"+gradeLevel+"/"+topics[i]+"/q"+(j+1)+".txt";
    					fileReader = new BufferedReader(new InputStreamReader(assets.open("questions/"+gradeLevel+"/"+topics[i]+"/q"+(j+1)+".txt")));
    					String tempQuestion = "";
    					// read the question file line by line until the file is ended.
    					do{
    						temp = fileReader.readLine();
    						tempQuestion = tempQuestion + ((temp != null) ? temp : "") ;
    					}while(temp != null);
    					// put the question in the array of questions
    					questions[i][j] = tempQuestion;
    					
    					// reading the answer
    					fileReader = new BufferedReader(new InputStreamReader(assets.open("questions/"+gradeLevel+"/"+topics[i]+"/a"+(j+1)+".txt")));
    					String tempAnswer = "";
    					// read the answer file line by line until the file is ended.
    					do{
    						temp = fileReader.readLine();
    						tempAnswer = tempAnswer + ((temp != null) ? temp : "") ;
    					}while(temp != null);
    					// put the answer in the array of answers
    					answers[i][j] = tempAnswer;
    				}
    			}
    		}
    		catch (IOException iOException){
    			Log.e("Virtual Math Contest", "Error loading question:" + currentQuestion);
    		}
    	}
    	
    	// private event listener for "Pick a question" button
    	private class PickQuestionButtonListener implements OnClickListener{
    		@Override
    		public void onClick(View v){
    			// give an update about questions and answers
    			loadQuestionsAndAnswers();
        		// Create an AlertDialog Builder for creating dialogs
        		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        		// create a dialog for choosing the topic
        		builder.setTitle(R.string.pickTopicPromptText);
        		builder.setItems(topics, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int whichTopic) {
						numTopic = whichTopic;
						// for a topic selected, using a dialog to 
						// ask which question the user is going to use.
						AlertDialog.Builder subDialogBuilder = new AlertDialog.Builder(getActivity());
						subDialogBuilder.setTitle(R.string.pickQuestionPromptText);
						// get selections for the dialog according to number of questions.
						String[] selections = new String[questions[numTopic].length];
						for (int i = 0; i< selections.length ; i++){
							selections[i] = "Question " + (i+1);
						}
						subDialogBuilder.setItems(selections, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int whichQuestion) {
								// generate the question according to user choices.
								numQuestion = whichQuestion;
								// pass in the 
				    			setQuestionInfo(topics[numTopic] + ", Question " + (numQuestion + 1),
		    							questions[numTopic][numQuestion],
		    							answers[numTopic][numQuestion],
		    							numQuestion + 1);
							}
						});
						AlertDialog questionSelectionDialog = subDialogBuilder.create();
						questionSelectionDialog.show();
					}
        		});
        		
        		AlertDialog topicSelectionDialog = builder.create();
        		topicSelectionDialog.show();
    		}
    	}
    	
    	// private event listener for "Give me a question!" button
    	private class RandomQuestionButtonListener implements OnClickListener{
    		@Override
    		public void onClick(View v){
    			// give an update about questions and answers
    			loadQuestionsAndAnswers();
    			int numTopic = randGenerator.nextInt(questions.length);
    			int numQuestion = randGenerator.nextInt(questions[numTopic].length);
    			setQuestionInfo(topics[numTopic] + ", Question " + (numQuestion + 1),
    							questions[numTopic][numQuestion],
    							answers[numTopic][numQuestion],
    							numQuestion + 1);
    		}
    	}
    }
    
    // Class representing Speed Arithmetics fragment.
    // It is required by Android API that an inner subclass of Fragment is public and static.
    public static class SpeedArithmeticFragment extends QuestionFragment{
    	private EditText questionPromptEditText;
    	private Button startButton;
    	private Button stopButton;
    	// the count down timer for timing a question
    	private SpeedArithmeticsCountDownTimer countDownTimer;
    	private int numQuestions;
    	private int numCurrentQuestion;
		private static final char[] OPREATORS = {'+','-','x','/'};
		// the amount of time given for one question in milliseconds
		private static final int MILLIS_PER_QUESTION = 30000;
		// the amount of point per question.
		private static final int POINT_VALUE = 1;
    	
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    		// get the view of this fragment from xml layout file.
    		View rootView = inflater.inflate(R.layout.fragment_speed_arithmetic, container, false);
    		return rootView;
    	}
    	
    	public void onActivityCreated(Bundle savedInstanceState){
    		super.onActivityCreated(savedInstanceState);
    		// make instance variables in superclass reference to the View in fragment_speed_arithmetic.xml 
    		questionInfoTextView = (TextView) getActivity().findViewById(R.id.speedArithmeticQuestionInfoTextView);
    		questionTextView = (TextView) getActivity().findViewById(R.id.speedArithmeticQuestionTextView);
    		submitButton = (Button)getActivity().findViewById(R.id.speedArithmeticSubmitButton);
    		answerEditText = (EditText) getActivity().findViewById(R.id.speedArithmeticAnswerEditText);
    		resultImageView = (ImageView) getActivity().findViewById(R.id.speedArithmeticResultImageView);
    		resultTextView = (TextView) getActivity().findViewById(R.id.speedArithmeticResultTextView);
    		
    		// get id of the widgets needed in SpeedArithmeticFragment
    		questionPromptEditText = (EditText) getActivity().findViewById(R.id.questionPromptEditText);
    		startButton = (Button) getActivity().findViewById(R.id.startButton);
    		stopButton = (Button) getActivity().findViewById(R.id.stopButton);
    		
    		// Set The text to be shown on startButton and stopButton
    		startButton.setText(R.string.startText);
    		stopButton.setText(R.string.stopText);
    		
    		// Make stopButton disabled
    		stopButton.setEnabled(false);
    		
    		// create a count down timer with call to onTick() method for each 1000 milliseconds.
    		countDownTimer = new SpeedArithmeticsCountDownTimer(MILLIS_PER_QUESTION,1000);
    		
    		// register submitButton to SubmitButtonListener
    		submitButton.setOnClickListener(new SubmitButtonListener());
    		
    		// Add listener to startButton and stopButton
    		startButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					try{
						// get number of questions going to be in one session 
						// from questionPromptEditText
						numQuestions = Integer.parseInt(questionPromptEditText.getText().toString());
						numCurrentQuestion = 0;
						nextQuestion();
						startButton.setEnabled(false);
						stopButton.setEnabled(true);
					}
					// do nothing if user pressed startButton without 
					// entering a value in questionPromptEditText.
					catch(NumberFormatException numberFormatException){}
				}

    		});
    		stopButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					stopSession();
				}
    		});
    	}
    	
    	// private helper method for stop current speed arithmetics session.
    	private void stopSession(){
			countDownTimer.cancel();
			questionInfoTextView.setText(getString(R.string.stoppedText));
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			setQuestionsViewVisibilty(View.INVISIBLE);
			setResultsViewVisibilty(View.INVISIBLE);
    	}
    	
    	// This method loads next question
    	// the question is generated according to grade levels.
    	private void nextQuestion() {
    		// stop and return if it's already last question.
    		if (numQuestions == numCurrentQuestion){
    			stopSession();
    			return;
    		}
    		
    		numCurrentQuestion++;
    		int firstOpreand = 0;
    		int secondOpreand = 0;
    		char opreator = 0;
    		
			if (gradeLevel == GRADE1TO2){
				// Operands is from range [0,100]
				firstOpreand = randGenerator.nextInt(101);
				secondOpreand = randGenerator.nextInt(101);
				opreator = OPREATORS[randGenerator.nextInt(2)];
			}
			else if (gradeLevel == GRADE3TO4){
				// first operand is from range [0,1000]
				firstOpreand = randGenerator.nextInt(1001);
				opreator = OPREATORS[randGenerator.nextInt(4)];
				// second operand is from range [0,1000] , or [1,10] in the case of division ('/')
				secondOpreand = (opreator == '/') ? randGenerator.nextInt(10) + 1 : randGenerator.nextInt(1001);
			}
			else if (gradeLevel == GRADE5TO6){
				// first operand are from range [-1000,1000]
				firstOpreand = randGenerator.nextInt(2001)-1000;
				opreator = OPREATORS[randGenerator.nextInt(4)];
				// second operand are from range [-1000,1000] , or [-10,10] (excluding 0) if opreator is division ('/')
				secondOpreand = (opreator == '/') ? 
						(randGenerator.nextInt(10) + 1) * (randGenerator.nextBoolean() ? -1 : 1)
						: randGenerator.nextInt(2001)-1000;
			}
			
			// Generate answer string
			String answerText;
			if (opreator == '+'){
				answerText = "" + (firstOpreand + secondOpreand);
			}
			else if(opreator == '-'){
				// if the grade level is not grade 5 to 6, 
				// then make firstOpreand larger than secondOpreand for avoiding negative results.
				if (gradeLevel != GRADE5TO6 && firstOpreand < secondOpreand){
					int temp = firstOpreand;
					firstOpreand = secondOpreand;
					secondOpreand = temp;
				}
				answerText = "" + (firstOpreand - secondOpreand);
			}
			else if(opreator == 'x'){
				answerText = "" + (firstOpreand * secondOpreand);
			}
			else{
				answerText = "" + (firstOpreand / secondOpreand + firstOpreand % secondOpreand);
			}
			// set the questionText to be the question 
			String questionText = ((opreator == '/') ? "The sum of quotient and remainder of " : "") 
					+ firstOpreand + " " + opreator + " " +
					((secondOpreand < 0) ? "(" : "") + secondOpreand + ((secondOpreand < 0) ? ")" : "") ;
			// Set question information with an empty string for SpeedArithmeticQuestionInfoTextView
			// ( The text will be set by SpeedArithmeticCountDownTimer.onTick() and onFinish() methods)
			setQuestionInfo("",questionText,answerText,POINT_VALUE);
			// start the count down timer.
			countDownTimer.start();
		}

		// private inner class for count down timer.
    	private class SpeedArithmeticsCountDownTimer extends CountDownTimer{
			public SpeedArithmeticsCountDownTimer(long millisInFuture,
					long countDownInterval) {
				super(millisInFuture, countDownInterval);
			}

			@Override
			public void onFinish() {
				questionInfoTextView.setText(getString(R.string.timesUpText) + "      " +
						getString(R.string.question) + " " + numCurrentQuestion + " " + getString(R.string.outOfText) +
						" " + numQuestions);
				// switch to next question since user didn't answer it.
				nextQuestion();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				questionInfoTextView.setText((millisUntilFinished/1000) + " " + getString(R.string.secondsLeftText) + "      " +
						getString(R.string.question) + " " + numCurrentQuestion + " " + getString(R.string.outOfText) +
						" " + numQuestions);
				if (getIsCorrect()){
					// pause for 2000 milliseconds (2 second) for showing the result
					pause(2000);
					nextQuestion();
				}
			}
    	}
    	
    	// private helper method for generating pauses in program.
    	// one integer parameter: the number of millisecond paused.
    	private void pause(int pauseTime){
    		try{
    			Thread.sleep(pauseTime);
    		}
    		catch(InterruptedException interruptedException){
    			Log.e("Virtual Math Contest", interruptedException.toString());
    		}
    	}
    	
    	// Method invoked when the fragment is paused.
    	public void onPause(){
    		super.onPause();
    		// stop the session so that the session and count down
    		// would not continue if the program (fragment) is paused.
    		stopSession();
    	}
    }
}
