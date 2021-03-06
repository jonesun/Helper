package jone.helper.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import jone.helper.bean.Notebook;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTEBOOK".
*/
public class NotebookDao extends AbstractDao<Notebook, Long> {

    public static final String TABLENAME = "NOTEBOOK";

    /**
     * Properties of entity Notebook.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property CreateDate = new Property(3, Long.class, "createDate", false, "CREATE_DATE");
        public final static Property UpdateDate = new Property(4, Long.class, "updateDate", false, "UPDATE_DATE");
        public final static Property Color = new Property(5, Integer.class, "color", false, "COLOR");
    };


    public NotebookDao(DaoConfig config) {
        super(config);
    }
    
    public NotebookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTEBOOK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TITLE\" TEXT," + // 1: title
                "\"CONTENT\" TEXT NOT NULL ," + // 2: content
                "\"CREATE_DATE\" INTEGER," + // 3: createDate
                "\"UPDATE_DATE\" INTEGER," + // 4: updateDate
                "\"COLOR\" INTEGER);"); // 5: color
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTEBOOK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Notebook entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindString(3, entity.getContent());
 
        Long createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(4, createDate);
        }
 
        Long updateDate = entity.getUpdateDate();
        if (updateDate != null) {
            stmt.bindLong(5, updateDate);
        }
 
        Integer color = entity.getColor();
        if (color != null) {
            stmt.bindLong(6, color);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Notebook readEntity(Cursor cursor, int offset) {
        Notebook entity = new Notebook( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // createDate
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // updateDate
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // color
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Notebook entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.getString(offset + 2));
        entity.setCreateDate(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setUpdateDate(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setColor(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Notebook entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Notebook entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
