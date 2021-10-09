package com.example.contactapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    private ArrayList<Contact> contactList;
    private ArrayList<Contact> tempList;
    private ClickListener clickListener;

    public ContactAdapter(ArrayList<Contact> list) {
        if(list!=null){
            contactList=list;
        }else{
            contactList=new ArrayList<>();
        }
        tempList=contactList;
    }

    public void setContactList(ArrayList<Contact> contactList) {
        if(contactList!=null){
            this.contactList=contactList;
        }else{
            this.contactList=new ArrayList<>();
        }
        tempList=this.contactList;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        Contact c=contactList.get(position);
        String firstLetter=c.getName().substring(0,1).toUpperCase();
        holder.getTvName().setText(c.getName());
        holder.getTvSection().setText(firstLetter);
        if(position > 0){
            int i = position - 1;
            if (firstLetter.equalsIgnoreCase(contactList.get(i).getName().substring(0,1))){
                System.out.println(firstLetter + " - true");
                holder.getTvSection().setVisibility(View.GONE);
            }else holder.getTvSection().setVisibility(View.VISIBLE);
        }
        byte[] contactThumbnail=c.getThumbnail();
        if(contactThumbnail!=null){
            Bitmap bitmap=BitmapFactory.decodeByteArray(contactThumbnail,0,contactThumbnail.length);
            holder.getIvThumbnail().setImageBitmap(bitmap);
            holder.getIvThumbnail().setVisibility(View.VISIBLE);
            holder.getIvThumbnailRect().setVisibility(View.GONE);
        }else{
            holder.getIvThumbnailRect().setVisibility(View.VISIBLE);
            holder.getIvThumbnail().setVisibility(View.GONE);
            int color= ((int)(Math.random()*16777215)) | (0xFF << 24);
            if(color==Color.WHITE || color==Color.BLACK) color=Color.RED;
            TextDrawable textDrawable=TextDrawable.builder()
                    .beginConfig()
                        .bold()
                    .endConfig()
                    .buildRound(firstLetter,color);
            holder.getIvThumbnailRect().setImageDrawable(textDrawable);
        }
    }

    @Override
    public int getItemCount() {
        if(contactList==null) {
            contactList=new ArrayList<>();
        }
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactList=tempList;
                } else {
                    ArrayList<Contact> filteredList = new ArrayList<>();
                    for (Contact row : tempList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getMobile().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    contactList=filteredList;
                    System.out.println(contactList.toString());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //contactList=(ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView tvName;
        private final TextView tvSection;
        private final CircleImageView ivThumbnail;
        private final ImageView ivThumbnailRect;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_contact_name);
            tvSection= view.findViewById(R.id.tv_contact_section);
            ivThumbnail=view.findViewById(R.id.iv_contact_image);
            ivThumbnailRect=view.findViewById(R.id.iv_contact_image_rect);
            view.setOnClickListener(this);
        }

        public TextView getTvName() {
            return tvName;
        }

        public TextView getTvSection() {
            return tvSection;
        }

        public CircleImageView getIvThumbnail(){return ivThumbnail;}

        public ImageView getIvThumbnailRect(){return ivThumbnailRect;}

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.itemClicked(contactList.get(getAdapterPosition()));
        }
    }

    public interface ClickListener {
        void itemClicked(Contact contact);
    }
}