package leon.homework.javaBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mjhzds on 2017/2/21.
 */

public class ShortExercise implements Parcelable{
    private int quesnum;
    private String result;
    private int isfinished = 0;
    private String shortId;
    private String title;
    private String answer;
    private String[] imgName;
    private int rank;

    public ShortExercise(String shortId,String title,String answer,String[] imgName,int rank) {
        this.shortId = shortId;
        this.title =title;
        this.answer = answer;
        this.imgName = imgName;
        this.rank = rank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shortId);
        dest.writeString(title);
        dest.writeString(answer);
        dest.writeStringArray(imgName);
        dest.writeInt(isfinished);
        dest.writeInt(rank);
    }

    public static final Creator<ShortExercise> CREATOR = new Creator<ShortExercise>() {
        @Override
        public ShortExercise createFromParcel(Parcel source) {
            String shortId = source.readString();
            String title = source.readString();
            String answer = source.readString();
            String[] imgname = new String[5];
            source.readStringArray(imgname);
            int isfinished = source.readInt();
            int rank = source.readInt();
            ShortExercise s = new ShortExercise(shortId,title,answer,imgname,rank);
            s.setIsfinished(isfinished);
            return s;
        }

        @Override
        public ShortExercise[] newArray(int size) {
            return new ShortExercise[0];
        }
    };

    public String[] getImgname(){
        return imgName;
    }
    public String getResult() {
        return result;
    }
    public String getAnswer() {
        return answer;
    }
    public String getShortId() {
        return shortId;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public int getQuesnum() {
        return quesnum;
    }

    public void setQuesnum(int quesnum) {
        this.quesnum = quesnum;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIsfinished() {
        return isfinished;
    }

    public void setIsfinished(int isfinished) {
        this.isfinished = isfinished;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
