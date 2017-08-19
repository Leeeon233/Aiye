package leon.homework.javaBean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mjhzds on 2017/2/21.
 */

public class ChoiceExercise implements Parcelable {

    private int isfinished = 0;
    private int quesnum;
    private int rank;
    private String choId;
    private String title;
    private String[] choices;
    private String[] imgname;
    public String getTitle() {
        return title;
    }
    private String answer;
    private String result;

    public String[] getChoices() {
        return choices;
    }

    public ChoiceExercise(String choId, String title, String[] choices, String[] imgname, String answer,int rank) {
        this.choId = choId;
        this.title = title;
        this.choices = choices;
        this.imgname =imgname;
        this.answer = answer;
        this.rank = rank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(choId);
        dest.writeString(title);
        dest.writeStringArray(imgname);
        if (choices == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(choices.length);
        }
        if (choices != null) {
            dest.writeStringArray(choices);
        }
        dest.writeString(answer);
        dest.writeInt(isfinished);
        dest.writeInt(rank);
    }

    public static final Creator<ChoiceExercise> CREATOR = new Creator<ChoiceExercise>() {
        @Override
        public ChoiceExercise createFromParcel(Parcel source) {
            String choId = source.readString();
            String title = source.readString();
            String[] imgname = new String[5];
            source.readStringArray(imgname);
            int length = source.readInt();
            String[] choices = null;
            if (length > 0) {
                choices = new String[length];
                source.readStringArray(choices);
            }
            String answer = source.readString();
            int isfinished = source.readInt();
            int rank = source.readInt();
            ChoiceExercise c = new ChoiceExercise(choId,title,choices,imgname,answer,rank);
            c.setIsfinished(isfinished);
            return c;
        }

        @Override
        public ChoiceExercise[] newArray(int size) {
            return new ChoiceExercise[0];
        }
    };

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String[] getImgname(){
        return imgname;
    }

    public String getAnswer() {
        return answer;
    }

    public int getQuesnum() {
        return quesnum;
    }

    public String getChoId() {
        return choId;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
