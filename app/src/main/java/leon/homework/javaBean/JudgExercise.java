package leon.homework.javaBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mjhzds on 2017/2/21.
 */

public class JudgExercise implements Parcelable{
    private int isfinished = 0;
    private int quesnum;
    private String result;
    private String judgeId;
    private String title;
    private String answer;
    private String imgName;
    public JudgExercise(String judgeId,String title,String answer,String imgName) {
        this.judgeId = judgeId;
        this.title = title;
        this.answer = answer;
        this.imgName = imgName;
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
        dest.writeString(judgeId);
        dest.writeString(title);
        dest.writeString(answer);
        dest.writeString(imgName);
        dest.writeInt(isfinished);
    }

    public static final Creator<JudgExercise> CREATOR = new Creator<JudgExercise>() {
        @Override
        public JudgExercise createFromParcel(Parcel source) {
            String judgeId = source.readString();
            String title = source.readString();
            String answer = source.readString();
            String img = source.readString();
            int isfinished = source.readInt();
            JudgExercise j = new JudgExercise(judgeId,title,answer,img);
            j.setIsfinished(isfinished);
            return j;
        }

        @Override
        public JudgExercise[] newArray(int size) {
            return new JudgExercise[0];
        }
    };

    public String getImgName() {
        return imgName;
    }

    public String getResult() {
        return result;
    }
    public String getJudgeId() {
        return judgeId;
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

    public String getAnswer() {
        return answer;
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
}
