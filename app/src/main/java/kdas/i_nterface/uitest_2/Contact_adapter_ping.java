package kdas.i_nterface.uitest_2;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by Interface on 15/08/16.
 */
public class Contact_adapter_ping extends RecyclerView.Adapter<Contact_adapter_ping.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView cname, cnum;
        public ImageView conimage;
        public FloatingActionButton fab1, fab2;
        public CoordinatorLayout rv_a;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);


            this.rv_a = (CoordinatorLayout) itemView.findViewById(R.id.base_coord);

            this.cname = (TextView)itemView.findViewById(R.id.tv1);
            this.cnum = (TextView)itemView.findViewById(R.id.tv2);
            this.conimage = (ImageView)itemView.findViewById(R.id.con_image_ping);
            //this.fab1 = (FloatingActionButton)itemView.findViewById(R.id.fab);
            this.fab2 = (FloatingActionButton)itemView.findViewById(R.id.fab2_ping);
            this.context = context;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            //Toast.makeText(context, "" + cardtext.getText() + pos + "", Toast.LENGTH_SHORT).show();

            if (pos == 0) {
                Intent i = new Intent(context, Main2Activity.class);
                context.startActivity(i);
            }

        }
    }



    private List<Contacts> contacts;
    private Context mcontext;

    public Contact_adapter_ping(Context context, List<Contacts> mContacts){
        contacts = mContacts;
        mcontext = context;
    }

    private Context getContext(){
        return mcontext;
    }


    @Override
    public Contact_adapter_ping.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.contact_row_ping, parent, false);
        ViewHolder viewHolder = new ViewHolder(getContext(), row);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Contact_adapter_ping.ViewHolder holder, int position) {

        final Contacts condata = contacts.get(position);

        TextView con_name = holder.cname;
        TextView con_num = holder.cnum;
        ImageView image_con = holder.conimage;
        FloatingActionButton f2;
        f2 = holder.fab2;

        TextDrawable condraw = TextDrawable.builder().buildRoundRect(condata.contact_alph, R.color.grey, 15);

        con_name.setText(condata.contact_name);
        con_num.setText(condata.contact_num);
        image_con.setImageDrawable(condraw);


        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = condata.contact_num;

                Intent i = new Intent(getContext(), MapsActivity.class);
                i.putExtra("pinger_num", num);
                getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
