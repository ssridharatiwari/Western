package com.milk.milkcollection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.ShowMember;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MilkDBHelpers extends SQLiteOpenHelper {


	public static final String DATABASE_NAME = "MyDBName";


	public MilkDBHelpers(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}





	@Override
	public void onCreate(SQLiteDatabase a) {
		// TODO Auto-generated method stub
		a.execSQL("create table ratechart(Id Integer primary Key Autoincrement,fat text,snf text ,rate text)");
		a.execSQL("create table ratechartclr(Id Integer primary Key Autoincrement,fat text,snf text ,rate text)");
		a.execSQL("create table member(Id Integer primary Key Autoincrement,membername text,membercode text,membermobile text,alldetails text)");
		a.execSQL("create table member_pay(Id Integer primary Key Autoincrement,membername text,membercode text,pay_amount text,date text)");
		a.execSQL("create table updatebhav(Id Integer primary Key Autoincrement,fromfat text,tofat text,fromsnf text,tosnf text,kgfatrat text,kgsnfrat text,comitionliter text,allfatsaf text,commissionType text)");
		a.execSQL("create table milk_amount(Id Integer primary Key Autoincrement,memberCode text,milkweight text,rateperliter text ,totalamount text,date text,milkinformation text," + "sift text,fat text,fat_wt text,snf text,snf_wt text,allInformation text,dailyInformation text,dateSave text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase a, int arg1, int arg2) {
		// TODO Auto-generated method stub
//		a.execSQL("drop table member");
//		a.execSQL("drop table updatebhav");
//		a.execSQL("drop table milk_amount");
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
		values.put("rateperliter",rateliter);
		values.put("totalamount",amount);
		values.put("date",mydate);
		values.put("milkinformation",number);
		values.put("sift",sift);
		values.put("fat",fat);
		values.put("fat_wt",fat_wt);
		values.put("snf",snf);
		values.put("snf_wt",snt_wt);
		values.put("allInformation",message);
		values.put("dailyInformation",printString);
		values.put("dateSave",dateSave);

        db.insert("milk_amount", null, values);
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
	public void addRateClr(String fat, String snf, String rate) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("snf", snf);
		values.put("rate", rate);
		values.put("fat", fat);

		db.insert("ratechartclr", null, values);
		db.close();
	}

    public void updateRate(String rate_id, String rate ) {

        String query = "UPDATE ratechart SET rate='" + rate + "' WHERE Id ='" + rate_id + "'";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

	public void updateRateClr(String rate_id, String rate ) {
		String query = "UPDATE ratechartclr SET rate='" + rate + "' WHERE Id ='" + rate_id + "'";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(query);
		db.close();
	}


	public void emptyClrTable() {
		String query = "delete FROM ratechartclr";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(query);
		db.close();
	}

	public void emptyfatSnf() {
		String query = "delete FROM ratechart";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(query);
		db.close();
	}



	public void newmember(String name, String code, String mobile, String alldetails) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("membername", name);
		values.put("membercode", code);
		values.put("membermobile", mobile);
		values.put("alldetails", alldetails);

		db.insert("member", null, values);
		db.close();
	}

	public void changebhav(String from_fat, String to_fat, String from_snf, String to_snf,
						   String kgfat_rat, String kgsnf_rat, String comition_liter,
						   String allfatsnfdata, String type) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
		values.put("fromfat", from_fat);
		values.put("tofat", to_fat);
		values.put("fromsnf", from_snf);
		values.put("tosnf", to_snf);
		values.put("kgfatrat", kgfat_rat);
		values.put("kgsnfrat", kgsnf_rat);
		values.put("comitionliter", comition_liter);
		values.put("allfatsaf", allfatsnfdata);
		values.put("commissionType", type);

        db.insert("updatebhav", null, values);
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

	public ArrayList<Integer> reportId() {
		ArrayList<Integer> reportIdList = new ArrayList<>();
		String selectQuery = "SELECT * FROM milk_amount";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int search;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getInt(0);
				reportIdList.add(search);
			} while (cursor.moveToNext());

		}
		return reportIdList;
	}


	public ArrayList<String> searchfatsnf()
	{
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

	public void fatSnf_delete(int fatSnfId) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM updatebhav WHERE Id='" + fatSnfId + "'");
		db.close();
	}

	public void deleteReport(int reportId) {

		String query = "DELETE FROM milk_amount WHERE Id='" + reportId + "'";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(query);
		db.close();
	}


	public int getMember() {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "select count(*) from " + "member";
		Cursor mCount = db.rawQuery(query, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		if (count > 0)
			return count;
		else
			return 0;
	}

	public String getMemberNameByCode(String code)
    {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT membername FROM member WHERE memberCode='"+code+"'";
		Cursor cursor = db.rawQuery(query, null);
		return cursor.getString(cursor.getColumnIndex("membername")).toString();
	}

	public int getupdatebhav() {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "select count(*) from " + "updatebhav";
		Cursor mCount = db.rawQuery(query, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		if (count > 0)
			return count;
		else
			return 0;
	}

	public ArrayList<String> searchmilk() {
		ArrayList<String> milklist = new ArrayList<String>();
		String selectQuery = "SELECT * FROM milk_amount";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(6);
				milklist.add(search);
			} while (cursor.moveToNext());

		}
		return milklist;
	}

	public ArrayList<String> searchmobilenumber() {

		ArrayList<String> membermobilelist = new ArrayList<String>();
		membermobilelist.add("Select");
		String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(3);
				membermobilelist.add(search);
			} while (cursor.moveToNext());

		}
		return membermobilelist;
	}

	public ArrayList<String> searchMemberCode() {

        ArrayList<String> memberCodeList = new ArrayList<String>();
		memberCodeList.add("Select");
		memberCodeList.add("All");
        String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;

        if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(2);
				memberCodeList.add(search);
			} while (cursor.moveToNext());
		}
		return memberCodeList;
	}


	public ArrayList<ShowMember> getMembers() {

        ArrayList<ShowMember>memberCodeList = new ArrayList<>();
		String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				ShowMember showMember = new ShowMember();
				showMember.setMember_code(cursor.getString(cursor.getColumnIndex("membercode")));
				showMember.setMember_contact(cursor.getString(cursor.getColumnIndex("membermobile")));
				showMember.setMember_name(cursor.getString(cursor.getColumnIndex("membername")));
				memberCodeList.add(showMember);

				Log.e(" cursor in members ",cursor.getString(cursor.getColumnIndex("membercode"))+" "+
						cursor.getString(cursor.getColumnIndex("membermobile"))+" "+
						cursor.getString(cursor.getColumnIndex("membername")));
			} while (cursor.moveToNext());
		}
		return memberCodeList;
	}



	public ArrayList<String> SearchName() {
		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add("Select");
		nameList.add("All");
        String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;

		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(1);
				nameList.add(search);
			} while (cursor.moveToNext());

		}
		return nameList;
	}

	public ArrayList<String> searchmemberdetails() {
		ArrayList<String> memberlist = new ArrayList<String>();

        String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;

		if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(4);
				memberlist.add(search);
			} while (cursor.moveToNext());
		}

		return memberlist;
	}

	public ArrayList<String> addmemberShow() {
		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add("Select");
		nameList.add("All");
        String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String search = null;

        if (cursor != null && cursor.moveToFirst()) {
			do {
				search = cursor.getString(1);
				nameList.add(search);
			} while (cursor.moveToNext());
		}
		return nameList;
	}

	public String getCurrentDateFromPublic() {

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		month++;
		int monthlength = String.valueOf(month).length();
		int daylength = String.valueOf(day).length();

		String date = "";
		if(monthlength==1&&daylength==1){
			date = "0"+String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+String.valueOf(year);


		}else if(monthlength==1&&daylength>1){
			date = String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+""+String.valueOf(year);
		}else if(monthlength>1&&daylength==1){
			date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);
		}else if(monthlength>1&&daylength>1){
			date = String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+""+String.valueOf(year);
		}
		return  date;
	}



    public String getRatePerLiter(String fat,String snf)
    {

        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.instace);
        String rateMethod =  sharedPreferencesUtils.getRateMethodCode();

        if (rateMethod.equals("1"))
        {
        String  kgfatrat = "" ,kgsnfrat = "",comition = "" ,comissionType = "";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<String> ToFatList = new ArrayList<>();
        ArrayList<String> FromFatList = new ArrayList<>();
        ArrayList<String> ToSnfList = new ArrayList<>();
        ArrayList<String> FromSnfList = new ArrayList<>();
        ArrayList<String> KgFatRatList = new ArrayList<>();
        ArrayList<String> KgSnfRatList = new ArrayList<>();
        ArrayList<String> ComitionLiterList = new ArrayList<>();
        ArrayList<String> commissionTypeList = new ArrayList<>();

        try {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from updatebhav", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FromFatList.add(cursor.getString(1));
                    ToFatList.add(cursor.getString(2));
                    FromSnfList.add(cursor.getString(3));
                    ToSnfList.add(cursor.getString(4));
                    KgFatRatList.add(cursor.getString(5));
                    KgSnfRatList.add(cursor.getString(6));
                    ComitionLiterList.add(cursor.getString(7));
                    commissionTypeList.add(cursor.getString(9));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }

        for (int i = 0; i < FromFatList.size(); i++) {
            String fromfff = FromFatList.get(i).toString();
            String tofff = ToFatList.get(i).toString();
            String tosss = ToSnfList.get(i).toString();
            String fromsss = FromSnfList.get(i).toString();
            String kgFatrat = KgFatRatList.get(i).toString();
            String kgSnfRat = KgSnfRatList.get(i).toString();
            String comLtr = ComitionLiterList.get(i).toString();
            String comType = commissionTypeList.get(i).toString();

            if (Float.parseFloat(tofff) >= Float.parseFloat(fat) && Float.parseFloat(fromfff) <= Float.parseFloat(fat) && Float.parseFloat(tosss) >= Float.parseFloat(snf) && Float.parseFloat(fromsss) <= Float.parseFloat(snf)) {

                kgfatrat = kgFatrat;
                kgsnfrat = kgSnfRat;
                comition = comLtr;
                comissionType = comType;
            }
        }

        Float rateperltrr;

        if (KgFatRatList.equals(""))
        {
             return "0";
        }
        else
        {
            DecimalFormat df = new DecimalFormat("#.##");
            float rateltr_fat = ((Float.parseFloat(fat) * Float.parseFloat(kgfatrat)) / (100));
            float rateltr_snf = (Float.parseFloat(snf) * Float.parseFloat(kgsnfrat)) / 100;

            if ("Less -".equals(comissionType)) {
                 rateperltrr = rateltr_fat + rateltr_snf - Float.parseFloat(comition);

            } else {
                 rateperltrr = rateltr_fat + rateltr_snf + Float.parseFloat(comition);
            }



            return  String.valueOf(rateperltrr);
        }
        }
        else if (rateMethod.equals("2")){



            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("Select * From 'ratechart' WHERE fat ='" + fat + "' and snf ='" + snf + "'", null);

            if (cursor != null && cursor.moveToFirst()) {

                return  (String.valueOf(cursor.getString(cursor.getColumnIndex("rate"))));
            }
            else{

                return "0";
            }
        }else if (rateMethod.equals("3")){

            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From 'ratechartclr' WHERE fat ='" + fat + "' and snf ='" + snf + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                return  (String.valueOf(cursor.getString(cursor.getColumnIndex("rate"))));
            }else{
                return "0";
            }
        }

        else {
            return "0";
        }

    }

}