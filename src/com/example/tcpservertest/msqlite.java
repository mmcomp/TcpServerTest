package com.example.tcpservertest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class msqlite  extends SQLiteOpenHelper{
	static final String KALA_ABARGROUP_TABLE_NAME = "kala_abarGroup";
	static final String KALA_GROUP_TABLE_NAME = "kala_group";
	static final String KALA_MINIGROUP_TABLE_NAME = "kala_miniGroup";
	static final String KALA_TABLE_NAME = "kala";
	static final String CONF_TABLE_NAME = "conf";
	static final String OREDER_TABLE_NAME = "sefaresh";
	static final String OREDER_DET_TABLE_NAME = "sefaresh_det";
	private static final String KALA_ABARGROUP_TABLE_CREATE = "create table " + KALA_ABARGROUP_TABLE_NAME + " (id integer primary key,name text,en integer default 1,ord integer default 1);";
	private static final String KALA_GROUP_TABLE_CREATE = "create table " + KALA_GROUP_TABLE_NAME + " (id integer primary key,kala_abarGroup_id integer default -1,name text,toz text,en integer default 1,ord integer default 0,pic text);";
	private static final String KALA_MINIGROUP_TABLE_CREATE = "create table " + KALA_MINIGROUP_TABLE_NAME + " (id integer primary key,kala_group_id integer default -1,name text,pic text);";
	private static final String KALA_TABLE_CREATE = "create table " + KALA_TABLE_NAME + " (id integer primary key,kala_miniGroup_id integer default -1,name text,toz text,pic text,ghimat integer default 0);";
	private static final String CONF_TABLE_CREATE = "create table " + CONF_TABLE_NAME + " (id integer primary key,cKey text,cValue text);";
	private static final String ORDER_TABLE_CREATE = "create table " + OREDER_TABLE_NAME + " (id integer primary key,tarikh text);";
	private static final String ORDER_DET_TABLE_CREATE = "create table " + OREDER_DET_TABLE_NAME + " (id integer primary key,order_id integer,product_id integer,tedad integer);";

	
	private static final String DATABASE_NAME = "darma.db";
	private static final int DATABASE_VERSION = 1;
	public msqlite(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(KALA_ABARGROUP_TABLE_CREATE);
		db.execSQL(KALA_GROUP_TABLE_CREATE);
		db.execSQL(KALA_MINIGROUP_TABLE_CREATE);
		db.execSQL(KALA_TABLE_CREATE);
		db.execSQL(CONF_TABLE_CREATE);
		db.execSQL(ORDER_TABLE_CREATE);
		db.execSQL(ORDER_DET_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
}
