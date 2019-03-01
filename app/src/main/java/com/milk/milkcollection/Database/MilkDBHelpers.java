package com.milk.milkcollection.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.helper.AppString;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.Member;
import com.milk.milkcollection.model.SingleEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MilkDBHelpers extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "western";
	public MilkDBHelpers(Context context) {
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


	public void update(SingleEntry entry) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("memberCode",entry.getCode());
		values.put("milkweight",entry.getWeight());
		values.put("rateperliter",entry.getRate());
		values.put("totalamount",entry.getAmount());
		values.put("date",entry.getDate());
		values.put("sift",entry.getSift());
		values.put("fat",entry.getfat());
		values.put("fat_wt",String.valueOf(entry.getFatWt()));
		values.put("snf",entry.getSnf());
		values.put("snf_wt",String.valueOf(entry.getSnfWt()));
		values.put("dateSave",entry.getDatesave());

		db.update("milk_amount", values, "Id="+entry.getID(), null);
		///myDB.update(TableName, cv, "_id="+id, null);


		db.close(); // Closing database connection
	}


	public Boolean isAlredy(String date,String sift,String code) {

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "select * from milk_amount where memberCode=" + "'"+ code +"' AND date=" + "'"+ date +"'  AND sift=" + "'"+ sift +"'  ";;
		Cursor mCount = db.rawQuery(query, null);
		mCount.moveToFirst();


		if (mCount != null && mCount.moveToFirst()) {
			return true;
		}else{
			return  false;
		}
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





	public void updateRateByRow(String fat, String snf, String rate) {

		String query = "UPDATE ratechart SET rate='" + rate + "' WHERE fat ='" + fat + "' AND snf ='" + snf + "'";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(query);
		db.close();
	}

	public void updateRateClrByRow(String fat, String snf, String rate ) {
		String query = "UPDATE ratechartclr SET rate='" + rate + "'  WHERE fat ='" + fat + "' AND snf ='" + snf + "'";
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

	public String getMemberNameByCode(String code) {

		if (isUserExist(code)) {

			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT * FROM member WHERE membercode='"+code+"'";
			Cursor cursor = db.rawQuery(query, null);

			Member member = new Member();
			if (cursor != null && cursor.moveToFirst()) {
				while (cursor.isAfterLast() == false) {
					member.setCode(code);
					member.setID(cursor.getString(cursor.getColumnIndex("Id")).toString());
					member.setName(cursor.getString(cursor.getColumnIndex(AppString.memberTable.name)).toString());
					member.setMobile(cursor.getString(cursor.getColumnIndex(AppString.memberTable.mobile)).toString());
					cursor.moveToNext();
				}
			}
			return member.getName();
		} else{
			return "";
		}
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




	public ArrayList<String> memberCode() {

		ArrayList<String> memberCodeList = new ArrayList<String>();

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


	public ArrayList<String> memberCodeAutoComplet() {

		ArrayList<String> memberCodeList = new ArrayList<String>();

		String selectQuery = "SELECT * FROM member ORDER BY memberCode";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String code = "";

		if (cursor != null && cursor.moveToFirst()) {
			do {
				code = cursor.getString(2);

				memberCodeList.add(code);
			} while (cursor.moveToNext());
		}
		return memberCodeList;
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



	public Member getMember(String code) {

		if (isUserExist(code)) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT * FROM member WHERE membercode='"+code+"'";
			Cursor cursor = db.rawQuery(query, null);

			Member member = new Member();
			if (cursor != null && cursor.moveToFirst()) {
				while (cursor.isAfterLast() == false) {
					member.setCode(code);
					member.setID(cursor.getString(cursor.getColumnIndex("Id")).toString());
					member.setName(cursor.getString(cursor.getColumnIndex(AppString.memberTable.name)).toString());
					member.setMobile(cursor.getString(cursor.getColumnIndex(AppString.memberTable.mobile)).toString());
					cursor.moveToNext();
				}
			}

			return member;
		}else{
			Member member = new Member();
			return member;
		}
	}

	public boolean isUserExist(String code) {


		Log.e("code-- ",code);
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

		Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + code + "'", null);
		if (cursor != null && cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				cursor.moveToNext();
				return true;
			}
		}
		return false;
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


	public void importDB() {

		//preExportDB();

		String db_name = DATABASE_NAME;
		File tempFile = new File("/data/" +  MainActivity.getInstace().getPackageName() + "/databases/" + DATABASE_NAME);

		Log.e("ImportDb", String.valueOf(tempFile.getPath()));
		Log.e("ImportDb", String.valueOf(tempFile));
		File data = Environment.getDataDirectory();
		FileChannel source = null;
		FileChannel destination = null;

		String mainFileToimport = "/data/" + MainActivity.getInstace().getPackageName() + "/databases/" + db_name;
		String tempFileName = db_name;

		//File currentDB = new File(tempFile, tempFileName);
		File backupDB = new File(data, mainFileToimport);

		try {
			source = new FileInputStream(tempFile).getChannel();
			destination = new FileOutputStream(backupDB).getChannel();
			destination.transferFrom(source, 0, source.size());
			source.close();
			destination.close();
			Toast.makeText(MainActivity.instace, "Please wait", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void getAvgFatSnf(String date,String code) {


		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor;


		long backdate = Long.valueOf(date) - 10;
		cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE memberCode = '" + code + "' and date >= '" + backdate + "' and date <= '" + date + "' ORDER BY date", null);


		float weightTotal = 0;
		float fat_wt = 0;
		float snf_wt = 0;

//		float avgFat = Float.valueOf(df.format(fat_wt / weightTotal));
//		float avgSnf = Float.valueOf(df.format(snf_wt / weightTotal));


		String alldata ="";

		if (cursor != null && cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {

				weightTotal = weightTotal + 	cursor.getFloat(cursor.getColumnIndex("memberCode"));
				weightTotal = weightTotal + 	cursor.getFloat(cursor.getColumnIndex("fat_wt"));
				weightTotal = weightTotal + 	cursor.getFloat(cursor.getColumnIndex("snf_wt"));
				cursor.moveToNext();

				//   alldata = alldata + "\n" + getNewString( String.valueOf(cursor.getColumnIndex("milkweight")), String.valueOf(cursor.getColumnIndex("fat")), String.valueOf(cursor.getColumnIndex("snf")),String.valueOf(cursor.getColumnIndex("rateperliter")),String.valueOf(cursor.getColumnIndex("totalamount")),String.valueOf(cursor.getColumnIndex("dateSave")),String.valueOf(cursor.getColumnIndex("sift")));
			}

			float avgFat = Float.valueOf((fat_wt / weightTotal));
     		float avgSnf = Float.valueOf((snf_wt / weightTotal));

		}


	}


}