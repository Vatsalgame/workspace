package com.teamvat.budgetme;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class HomePage extends Activity {
	
	// declaring a DB handler
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		updateFields();
	}
	
	// if activity is paused and started again
	@Override
	public void onRestart() {
		super.onRestart();
		updateFields();
	}
	
//	// if activity gains focus after app is stopped
//	public void onResume() {
//		updateFields();
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}
	
	/** Called when user clicks on Add Your Expenses **/
	public void switchToAdd(View view) {
		Intent switchToAdd = new Intent(this, AddExpense.class);
		startActivity(switchToAdd);
	}
	
	/** Called when user clicks on Configure **/
	public void switchToConfig(View view) {
		Intent switchToConfig = new Intent(this, Configure.class);
		startActivity(switchToConfig);
	}
	
	/** Called when user clicks on Track Status **/
	public void switchToStats(View view) {
		Intent switchToStats= new Intent(this, TrackStatus.class);
		startActivity(switchToStats);
	}
	
	// to test DB reading
	public void refresh(View view) {
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// columns to read
		String[] projection = {
				BudgetEntry.COLUMN_NAME_EXPENSE_ID,
				BudgetEntry.COLUMN_NAME_EXPENSE_AMT,
				BudgetEntry.COLUMN_NAME_EXPENSE_CAT,
				BudgetEntry.COLUMN_NAME_EXPENSE_DATE
		};
		
		String selection = "Expense_Id > ?";
		String[] selectionArgs = {
				"0"
		};
		
		Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
									 projection, 
									 selection, 
									 selectionArgs, 
									 null, 
									 null, 
									 null);
		
//		rowPointer.moveToFirst();
		int id = 0;
		Double amt = 0.0;
		String category = "default";
		String date = "0000-00-00";
		while (rowPointer.moveToNext()) {
			id = rowPointer.getInt(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_ID));
			amt = rowPointer.getDouble(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_AMT));
			category = rowPointer.getString(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_CAT));
			date = rowPointer.getString(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_DATE));
		}
		
		TextView idText = (TextView) findViewById(R.id.idText);
		idText.setText("" + id);
		TextView amtText = (TextView) findViewById(R.id.amtText);
		amtText.setText("" + amt);
		TextView catText = (TextView) findViewById(R.id.catText);
		catText.setText(category);
		TextView dateText = (TextView) findViewById(R.id.dateText);
		dateText.setText(date);
	}
	
	// to set the main fields on home page
	public void updateFields() {
		// to get budget of the month
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		Float monthlyBudget = fieldValues.getFloat("monthlyBudget", 0.0f);
		TextView monBudgetText = (TextView) findViewById(R.id.mBudgetText);
		monBudgetText.setText("Budget for this month: " + monthlyBudget);
		// to get total expenses for the month
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// getting the date to get monthly expenditure
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	// readying the query
    	String[] projection = {
    		"sum("	+ BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
    	};
    	String selection = "Expense_Date like ?";
    	String[] selectionArgs = {
    			date.substring(0, 7) + "%"
    	};
    	
    	Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
    								 projection, 
    								 selection, 
    								 selectionArgs, 
    								 null, 
    								 null, 
    								 null);
    	Double totalMonthlyExpense = 0.0;
    	while (rowPointer.moveToNext()) {
    		totalMonthlyExpense = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
    	}
    	TextView monExpenses = (TextView) findViewById(R.id.moneySpentText);
    	monExpenses.setText("Money Spent: " + totalMonthlyExpense);
    	
    	Double moneyRemaining = monthlyBudget - totalMonthlyExpense;
    	TextView remText = (TextView) findViewById(R.id.remainderText);
    	remText.setText("Money Remaining: " + moneyRemaining);    	
	} 

}