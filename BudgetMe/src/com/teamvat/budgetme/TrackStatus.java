package com.teamvat.budgetme;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TrackStatus extends Activity {
	
	BudgetDbHelper bDbHelper;
	public static String[] statVariants = {
		"Stats (to Date)", "Stats (this Month)", "Stats (this Year)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_status);
		updateStats();
		Spinner spinner = (Spinner) findViewById(R.id.statLabel);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, R.layout.spinner_item, statVariants);
//		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(populator);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		updateStats();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_status, menu);
		return true;
	}
	
	public void updateStats() {
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// write a similar code as following for different categories
		String[] projection = {
				"sum(" + BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
		};
		Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
				 projection, 
				 null, 
				 null, 
				 null, 
				 null, 
				 null);
		Double totalExpense = 0.0;
		while (rowPointer.moveToNext()) {
    		totalExpense = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
    	}
		TextView totalExpenses = (TextView) findViewById(R.id.totalExpense);
    	totalExpenses.setText("Total Expenditure (to date): " + totalExpense);
    	
    	// to populate all the views
    	TextView billExpenses = (TextView) findViewById(R.id.catBills);
    	TextView eduExpenses = (TextView) findViewById(R.id.catEducation);
    	TextView foodExpenses = (TextView) findViewById(R.id.catFood);
    	TextView gasExpenses = (TextView) findViewById(R.id.catGas);
    	TextView groExpenses = (TextView) findViewById(R.id.catGroceries);
    	TextView rentExpenses = (TextView) findViewById(R.id.catRent);
    	TextView repExpenses = (TextView) findViewById(R.id.catRepairs);
    	TextView othExpenses = (TextView) findViewById(R.id.catOthers);
    	
    	TextView[] catTextList = {
    			billExpenses, eduExpenses, foodExpenses, gasExpenses, groExpenses, rentExpenses, repExpenses, othExpenses
    	};
    	Double[] catExpenses = new Double[AddExpense.categories.length];
    	String selection = "Category like ?";
    	String [] selectionArgs = new String[1];
    	for(int i = 0; i < AddExpense.categories.length; i++) {
    		catExpenses[i] = 0.0;
    		selectionArgs[0] = AddExpense.categories[i];
    		rowPointer = db.query(BudgetEntry.TABLE_NAME, 
					 projection, 
					 selection, 
					 selectionArgs, 
					 null, 
					 null, 
					 null);
    		while (rowPointer.moveToNext()) {
        		catExpenses[i] = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
        	}
    		catTextList[i].setText(AddExpense.categories[i] + ": " + catExpenses[i]);    		
    	}
	}

}
