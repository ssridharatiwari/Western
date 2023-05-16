package com.milk.milkcollectionapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.milk.milkcollectionapp.helper.AppString;
import com.milk.milkcollectionapp.model.Member;
import com.milk.milkcollectionapp.model.SingleEntry;

import java.util.ArrayList;

public class MilkDBOld extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "western";
	public MilkDBOld(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}


	@SuppressLint("LongLogTag")
	@Override
	public void onCreate(SQLiteDatabase a) {
		// TODO Auto-generated method stub

		// -- -- Log.e("table created ----  ---- ",a.getPath());
		a.execSQL("create table ratechart(Id Integer primary Key Autoincrement,fat text,snf text ,rate text)");
		a.execSQL("create table ratechartclr(Id Integer primary Key Autoincrement,fat text,snf text ,rate text)");
		a.execSQL("create table member(Id Integer primary Key Autoincrement,membername text,membercode text,membermobile text,alldetails text)");
		a.execSQL("create table member_pay(Id Integer primary Key Autoincrement,membername text,membercode text,pay_amount text,date text)");
		a.execSQL("create table updatebhav(Id Integer primary Key Autoincrement,fromfat text,tofat text,fromsnf text,tosnf text,kgfatrat text,kgsnfrat text,comitionliter text,allfatsaf text,commissionType text)");
		a.execSQL("create table milk_amount(Id Integer primary Key Autoincrement,memberCode text,milkweight text,rateperliter text ,totalamount text,date text,"+ AppString.milk.number +" text," +
				"sift text,fat text,fat_wt text,snf text,snf_wt text,allInformation text,dailyInformation text,dateSave text)");
		a.execSQL("create table sell_data(Id Integer primary Key Autoincrement,weight text,rate text,amount text,date text,sift text,fat text,snf text,dateSave text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase a, int arg1, int arg2) {
		// TODO Auto-generated method stub

		onCreate(a);

	}

	public void updateMember(int memberId, String name, String code, String mobile, String allEntity) {

        SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("membername", name);
		values.put("membercode", code);
		values.put("membermobile", mobile);
		values.put("alldetails", allEntity);
		db.update("member", values, "Id" + "=" + memberId, null);

	}

	public void AddMilk(String memberCode, String weight, float rateliter, float amount, String mydate,
                        String number, String sift, String fat, Float fat_wt, String snf, Float snt_wt,
                        String message, String printString, String dateSave) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
        values.put("memberCode",memberCode);
        values.put("milkweight",weight);
		values.put("rateperliter", String.valueOf( rateliter));
		values.put("totalamount", String.valueOf( amount));
		values.put("date",mydate);
		values.put("sift",sift);
		values.put("fat",fat);
		values.put("fat_wt", String.valueOf( fat_wt));
		values.put("snf",snf);
		values.put("snf_wt", String.valueOf( snt_wt));
		values.put("allInformation",message);
		values.put("dailyInformation",printString);
		values.put("dateSave",dateSave);

        db.insert("milk_amount", null, values);


		db.close(); // Closing database connection
    }



	public void AddSellData(SingleEntry entry) {

		Log.e("values" ,entry.getAmount());
		Log.e("date" ,entry.getDate());
		Log.e("weight" ,entry.getWeight());


		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("weight",entry.getWeight());
		values.put("rate",entry.getRate());
		values.put("amount",entry.getAmount());
		values.put("date",entry.getDate());
		values.put("sift",entry.getSift());
		values.put("fat",entry.getfat());
		values.put("snf",entry.getSnf());
		values.put("dateSave",entry.getDatesave());

		db.insert("sell_data",null,values);
		db.close();
	}


	public void update(String memberCode, String weight, float rateliter, float amount, String mydate,
						String number, String sift, String fat, Float fat_wt, String snf, Float snt_wt,
						String message, String printString, String dateSave,String id) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("memberCode",memberCode);
		values.put("milkweight",weight);
		values.put("rateperliter",rateliter);
		values.put("totalamount",amount);
		values.put("date",mydate);
		values.put("sift",sift);
		values.put("fat",fat);
		values.put("fat_wt",String.valueOf(fat_wt));
		values.put("snf",snf);
		values.put("snf_wt",String.valueOf(snt_wt));
		values.put("allInformation",message);
		values.put("dailyInformation",printString);
		values.put("dateSave",dateSave);

		db.update("milk_amount", values, "Id="+id, null);
		///myDB.update(TableName, cv, "_id="+id, null);


		db.close(); // Closing database connection
	}




	public void addRate(String fat, String snf, String rate) {


        SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("fat", fat);
		values.put("snf", snf);
		values.put("rate", rate);

        db.insert("ratechart", null, values);
		db.close();
	}




	public ArrayList<Integer> searchfatsnfId() {
		ArrayList<Integer> fatsnflistId = new ArrayList<>();
		String selectQuery = "SELECT * FROM updatebhav";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int search;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do{
                search = cursor.getInt(0);
				fatsnflistId.add(search);
			} while (cursor.moveToNext());
		}
		return fatsnflistId;
	}


	public ArrayList<String> searchfatsnf()  {
		ArrayList<String> fatsnflist = new ArrayList<String>();
		String selectQuery = "SELECT * FROM updatebhav";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(8);
				fatsnflist.add(search);
			} while (cursor.moveToNext());

		}
		return fatsnflist;
	}



	public ArrayList<Member> getMembers() {

        ArrayList<Member>memberCodeList = new ArrayList<>();
		String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null && cursor.moveToFirst()) {

			int count = 1;
			do {
				Member member = new Member();
				member.setCode(cursor.getString(cursor.getColumnIndex(AppString.memberTable.code)));
				member.setMobile(cursor.getString(cursor.getColumnIndex(AppString.memberTable.mobile)));
				member.setName(cursor.getString(cursor.getColumnIndex(AppString.memberTable.name)));
				member.setID(cursor.getString(cursor.getColumnIndex(AppString.memberTable.id)));
				member.setDetail(cursor.getString(cursor.getColumnIndex(AppString.memberTable.detail)));
				//showMember.setNo(String.get  count);

				memberCodeList.add(member);

			} while (cursor.moveToNext());
		}
		return memberCodeList;
	}






}