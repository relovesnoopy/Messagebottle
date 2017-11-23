package jp.ac.hal.messagebottle;

import java.util.Date;

/**
 * Created by muto.masakazu on 2017/09/04.
 */

public class FileEntity {
    private String Object_id;
    private String file;
    private String file_tag;
    private Date TimeStamp;

    public String getObject_id() {
        return Object_id;
    }

    public void setObject_id(String object_id) {
        Object_id = object_id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile_tag() {
        return file_tag;
    }

    public void setFile_tag(String file_tag) {
        this.file_tag = file_tag;
    }

    public Date getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        TimeStamp = timeStamp;
    }


}
