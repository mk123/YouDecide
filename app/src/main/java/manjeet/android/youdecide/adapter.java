package manjeet.android.youdecide;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by manjeet on 9/5/16.
 */
public class adapter extends RecyclerView.Adapter<adapter.MyHolder> {

    Context context;
    ArrayList<ShowQuestions.Question> AllQuestions;
    LayoutInflater inflater;
    public static int[] selectedOption;
    static int questionsAnswered=0;
    static int totalQuestions=0;
    public adapter(Context _context,ArrayList<ShowQuestions.Question> questions)
    {
        context=_context;
        AllQuestions=questions;

        totalQuestions=AllQuestions.size();
        //Toast.makeText(_context, "totalQuestions "+AllQuestions.size(), Toast.LENGTH_SHORT).show();
        ///initially set selected options to -1 i.e. no option selected
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static void resetValues(int numberOfQuestions)
    {
        questionsAnswered=0;
        totalQuestions=0;
        selectedOption=new int[numberOfQuestions];
        for (int i = 0; i < numberOfQuestions; i++) {
            selectedOption[i]=-1;
        }

    }
    public class MyHolder extends RecyclerView.ViewHolder
    {
        TextView statement;
        LinearLayout optionsGroup;
        public MyHolder(View view)
        {
            super(view);
            optionsGroup=(LinearLayout)view.findViewById(R.id.optionsGroup);
            statement=(TextView)view.findViewById(R.id.statement);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.question_layout,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        ShowQuestions.Question question=AllQuestions.get(position);
        holder.statement.setText(question.statement);
        holder.optionsGroup.removeAllViews();
        for (int i = 0; i < question.options.size(); i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(10,0,0,0);
            textView.setText(question.options.get(i));
            textView.setTag(new int[]{position, i});
            textView.setOnClickListener(new textViewListener());
            ////if option is selected
            if(selectedOption[position]==i)
            {
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.selected_back);
                holder.optionsGroup.setTag(textView);
            }
            else
            {
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE);
            }
            holder.optionsGroup.addView(textView);
        }

    }

    public class textViewListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int[] tag=(int[])v.getTag();
            ////tag[0]=question number    tag[1]=selected option
            int previousOption=selectedOption[tag[0]];

            selectedOption[tag[0]]=tag[1];
            ((TextView)v).setTextColor(Color.WHITE);
            ((TextView)v).setBackgroundResource(R.drawable.selected_back);
            //Toast.makeText(context, "question="+tag[0]+" "+"option="+tag[1], Toast.LENGTH_SHORT).show();
            View parent=(View) v.getParent();
            ////if any previous option selected then change it's background to unselected
            if(previousOption!=(-1)) {
                TextView textView=(TextView)parent.getTag();
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE);
                parent.setTag(v);
            }
            else
            {
                parent.setTag(v);
                questionsAnswered++;
            }
        }
    }

    @Override
    public int getItemCount() {
        return AllQuestions.size();
    }


}
