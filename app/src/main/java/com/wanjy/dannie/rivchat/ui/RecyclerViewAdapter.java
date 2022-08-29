package com.wanjy.dannie.rivchat.ui;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viewpagerindicator.CirclePageIndicator;
import com.wanjy.dannie.rivchat.MySpannable;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import com.wanjy.dannie.rivchat.model.ImageUploadInfo;
import com.wanjy.dannie.rivchat.model.ImagesModel;
import com.wanjy.dannie.rivchat.util.ImageUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.ocpsoft.prettytime.PrettyTime;
import com.wanjy.dannie.rivchat.spannable.CustomQuoteSpan;
import com.wanjy.dannie.rivchat.spannable.URLImageParser;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import com.wanjy.dannie.rivchat.model.User;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

	Context context;
	List<ImageUploadInfo> MainImageUploadInfoList;
	DatabaseReference likeref, c, comment_seenref;
	PopupMenu popup;
	DatabaseReference commentnum;
	static String COMMENT_KEY = "PostComments";
	boolean likechecker = false;
	private int currentPage = 0;
	private int NUM_PAGES = 0;
	public List<String> listImages = new ArrayList<>();
	private DatabaseReference userDB;

	public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList) {

		this.MainImageUploadInfoList = TempList;
		this.context = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item2, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		popup = new PopupMenu(context, holder.itemView);
		popup.inflate(R.menu.chat_popu);


		ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);


		//holder.imageNameTextView.setText(UploadInfo.getImageName());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			holder.imageNameTextView.setText(Html.fromHtml(UploadInfo.getImageName(), Html.FROM_HTML_MODE_COMPACT));
		} else {
			holder.imageNameTextView.setText(Html.fromHtml(UploadInfo.getImageName()));
		}
			holder.imageNameTextView.setVisibility(View.GONE);

		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		String html = UploadInfo.getImageName();
		holder.webView.loadDataWithBaseURL("", html, mimeType, encoding, "");
		holder.webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);



		holder.Link.setText(UploadInfo.getImageURL());
		holder.ID.setText(UploadInfo.get_key());
		holder.UserID.setText(UploadInfo.getUser());


		String dateValue = UploadInfo.getDate();


		if (holder.slideImages.isEmpty()) {
			holder.Relative_Image.setVisibility(View.GONE);
		} else {
			if(holder.slideImages.size() > 0)
				holder.Relative_Image.setVisibility(View.VISIBLE);
		}

		DatabaseReference refdb = FirebaseDatabase.getInstance().getReference();
		refdb.child("All_Image_Uploads_Database").child(holder.ID.getText().toString()).addValueEventListener(new ValueEventListener() {
													   @Override
													   public void onDataChange(DataSnapshot dataSnapshot) {
															   for(DataSnapshot booksSnapshot : dataSnapshot.child("images").getChildren()){
																   String bookskey = booksSnapshot.getKey();
																   String booksValue = (String) booksSnapshot.getValue();
																   //Toast.makeText(context,""+booksValue,Toast.LENGTH_LONG).show();

																   listImages = new ArrayList<>();
																   listImages.add(booksValue);
																   holder.slideImages.add(booksValue);



																   if (holder.slideImages.isEmpty()) {
																	   holder.Relative_Image.setVisibility(View.GONE);
																   } else {
																	   if(holder.slideImages.size() > 0)
																		   holder.Relative_Image.setVisibility(View.VISIBLE);
																   }



																   holder.mPager.setAdapter(new SlidingImage_Adapter(context, holder.slideImages));
																   holder.indicator.setViewPager(holder.mPager);


																   NUM_PAGES = holder.slideImages.size();

																   final Handler handler = new Handler();
																   final Runnable Update = new Runnable() {
																	   public void run() {
																		   if (currentPage == NUM_PAGES) {
																			   currentPage = 0;
																		   }
																		   holder.mPager.setCurrentItem(currentPage++, true);
																	   }
																   };
																   Timer swipeTimer = new Timer();
																   swipeTimer.schedule(new TimerTask() {
																	   @Override
																	   public void run() {
																		   handler.post(Update);
																	   }
																   }, 3000, 3000);

																   // Pager listener over indicator
																   holder.indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
																	   @Override
																	   public void onPageSelected(int position) {
																		   currentPage = position;
																		   //Toast.makeText(context,""+holder.slideImages.get(position),Toast.LENGTH_LONG).show();

																	   }
																	   @Override
																	   public void onPageScrolled(int pos, float arg1, int arg2) {

																	   }
																	   @Override
																	   public void onPageScrollStateChanged(int pos) {

																	   }
																   });







															   }

													   }
													   @Override
													   public void onCancelled(DatabaseError databaseError) {

													   }
												   });




		holder.moreImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final SharedPreferences sharedd = context.getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);

				if ((sharedd.getString("title", "").contains("مندوب") || sharedd.getString("title", "").contains("مندوبة") || sharedd.getString("title", "").contains("المبرمج") || sharedd.getString("title", "").contains("المشرف") || sharedd.getString("title", "").contains("المشرفة")) && holder.slideImages.isEmpty()) {
					popup = new PopupMenu(context, holder.moreImage);
					popup.inflate(R.menu.post_manager);
					popup.show();

				} else {
					if ((sharedd.getString("title", "").contains("مندوب") || sharedd.getString("title", "").contains("مندوبة") || sharedd.getString("title", "").contains("المبرمج") || sharedd.getString("title", "").contains("المشرف") || sharedd.getString("title", "").contains("المشرفة")) && holder.slideImages.size() > 0) {
						popup = new PopupMenu(context, holder.moreImage);
						popup.inflate(R.menu.post_manger_link);
						popup.show();

					}else {
						if ((!sharedd.getString("title", "").contains("مندوب") || !sharedd.getString("title", "").contains("مندوبة") || !sharedd.getString("title", "").contains("المبرمج") || !sharedd.getString("title", "").contains("المشرف") || !sharedd.getString("title", "").contains("المشرفة")) && holder.slideImages.isEmpty()) {
							ClipboardManager clipboardd = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
							ClipData clipp = ClipData.newPlainText("labell", holder.imageNameTextView.getText().toString());
							clipboardd.setPrimaryClip(clipp);
							Toast.makeText(context,"تم نسخ الصورة",Toast.LENGTH_LONG).show();

						}else{
							if ((!sharedd.getString("title", "").contains("مندوب") || !sharedd.getString("title", "").contains("مندوبة") || !sharedd.getString("title", "").contains("المبرمج") || !sharedd.getString("title", "").contains("المشرف") || !sharedd.getString("title", "").contains("المشرفة")) && holder.slideImages.size() > 0) {
								popup = new PopupMenu(context, holder.moreImage);
								popup.inflate(R.menu.post_strange_image);
								popup.show();


							}





						}

					}


				}


				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
							case R.id.copy:
								ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText("labelo", holder.imageNameTextView.getText().toString());
								clipboard.setPrimaryClip(clip);
								Toast.makeText(context, "تم النسخ", Toast.LENGTH_LONG).show();

								break;
							case R.id.delete:
								DatabaseReference deleteComment = FirebaseDatabase.getInstance().getReference().child("All_Image_Uploads_Database").child(holder.ID.getText().toString());
								deleteComment.removeValue();
								break;
							case R.id.download:
								Toast.makeText(context, "تم الحفظ", Toast.LENGTH_SHORT).show();
								for (int i = 0; i < holder.slideImages.size(); i++) {
									//holder.downloadfile(holder.slideImages.get(i),"Image"+System.currentTimeMillis()+i);
									downloadFile(holder.slideImages.get(i),"Image");

								}


								break;
							case R.id.change:




								final AlertDialog dialog = new AlertDialog.Builder(context).create();
								LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View convertView = (View) inflater.inflate(R.layout.dialog_edit_post, null);
								dialog.setView(convertView);
								dialog.setCancelable(false);

								final EditText ImageNameEditText = convertView.findViewById(R.id.ImageNameEditText);
								Button accept = convertView.findViewById(R.id.acceptbtn);
								Button close = convertView.findViewById(R.id.closebtn);

								ImageNameEditText.setText(holder.imageNameTextView.getText().toString());

								accept.setOnClickListener(new View.OnClickListener(){
									@Override
									public void onClick(View p1)
									{
										dialog.dismiss();

										DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
										mDatabase.child("All_Image_Uploads_Database").child(holder.ID.getText().toString()).child("imageName").setValue(ImageNameEditText.getText().toString());
										holder.imageNameTextView.setText(ImageNameEditText.getText().toString());
										Toast.makeText(context,"تم تغيير النص بنجاح",Toast.LENGTH_LONG).show();

									}});

								close.setOnClickListener(new View.OnClickListener(){
									@Override
									public void onClick(View p1)
									{
										dialog.dismiss();


									}
								});

								dialog.show();


								break;


						}
						return false;
					}
				});




			}
		});












		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
		DatabaseReference NameGet = ref.child("user").child(holder.UserID.getText().toString());
		NameGet.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				holder.User_nam.setText(user.getName());
				holder.titleTxt.setText(user.getTitle());

				String img = user.getAvata();
				final Bitmap src;
				if (img.equals("default")) {
					holder.user_image.setImageResource(R.drawable.user_profile);
				} else {
					byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
					src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
					holder.user_image.setImageDrawable(ImageUtils.roundedImage(context, src));
				}

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});


		PrettyTime p = new PrettyTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(dateValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long millis = date.getTime();
		String datetime = p.format(new Date(millis));
		holder.datev.setText(datetime);


		likeref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {

				if (snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)) {
					holder.likebtn.setImageResource(R.drawable.ic_liked_btn);

					holder.likeColor.setTextColor(Color.parseColor("#0096DC"));
				} else {
					holder.likebtn.setImageResource(R.drawable.ic_like_btn);

					holder.likeColor.setTextColor(Color.parseColor("#000000"));
				}
				holder.likesnum.setText(String.valueOf(snapshot.child(holder.ID.getText().toString()).getChildrenCount()));

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


		commentnum.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				holder.commentsnumber.setText(String.valueOf(snapshot.child(holder.ID.getText().toString()).getChildrenCount() + " تعليقات"));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


		holder.commentbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent k = new Intent(context, CommentActivity.class);
				k.putExtra("postKey", holder.ID.getText().toString());
				context.startActivity(k);

			}
		});

		holder.commentsnumber.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent k = new Intent(context, CommentActivity.class);
				k.putExtra("postKey", holder.ID.getText().toString());
				context.startActivity(k);

			}
		});


		holder.likebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1) {
				likechecker = true;

				likeref.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot snapshot) {

						if (likechecker) {

							if (snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)) {
								likeref.child(holder.ID.getText().toString()).child(StaticConfig.UID).removeValue();
								likechecker = false;
							} else {
								likeref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("liked");
								likechecker = false;
							}
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

			}
		});
		holder.likeColor.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View p1) {
				likechecker = true;

				likeref.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot snapshot) {

						if (likechecker) {
							if (snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)) {
								likeref.child(holder.ID.getText().toString()).child(StaticConfig.UID).removeValue();
								likechecker = false;
							} else {
								likeref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("liked");
								likechecker = false;
							}
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

			}
		});


		holder.imageView.setVisibility(View.GONE);



		comment_seenref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {

				if (snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)) {
					//holder.like_btn.setImageResource(R.mipmap.action_like_accent);
				} else {
					// holder.like_btn.setImageResource(R.mipmap.action_like_gray);
					comment_seenref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("seen");
				}
				holder.seenTxt.setText(String.valueOf(snapshot.child(holder.ID.getText().toString()).getChildrenCount()));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


	}


	@Override
	public int getItemCount() {

		return MainImageUploadInfoList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		public ImageView imageView, user_image, moreImage;
		public TextView User_nam, imageNameTextView, likesnum, datev, commentsnumber, likeColor, seenTxt;
		public ImageView likebtn, commentbtn;
		TextView Link, ID, UserID, titleTxt;
		WebView webView;

		RelativeLayout Relative_Image;
		private ViewPager mPager;
		CirclePageIndicator indicator;
		public List<String> slideImages;

		public ViewHolder(final View itemView) {
			super(itemView);

			mPager = (ViewPager) itemView.findViewById(R.id.pager);
			indicator = (CirclePageIndicator)
					itemView.findViewById(R.id.indicator);

			final float density = context.getResources().getDisplayMetrics().density;
			indicator.setRadius(5 * density);

			slideImages = new ArrayList<>();


			User_nam = itemView.findViewById(R.id.blog_user_name);
			imageView = (ImageView) itemView.findViewById(R.id.imageView);
			moreImage = (ImageView) itemView.findViewById(R.id.moreImage);
			Relative_Image = itemView.findViewById(R.id.Relative_Image);

			commentbtn = itemView.findViewById(R.id.blog_comment_icon);
			likebtn = itemView.findViewById(R.id.blog_like_btn);
			imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
			datev = (TextView) itemView.findViewById(R.id.date);
			user_image = itemView.findViewById(R.id.blog_user_image);
			titleTxt = (TextView) itemView.findViewById(R.id.titleTxt);
			likeColor = (TextView) itemView.findViewById(R.id.like_color);
			seenTxt = (TextView) itemView.findViewById(R.id.Seen_num);

			webView = itemView.findViewById(R.id.WebView);
			//imageNameTextView.setVisibility(View.GONE);

			Link = (TextView) itemView.findViewById(R.id.Link);
			ID = (TextView) itemView.findViewById(R.id.ID);
			UserID = (TextView) itemView.findViewById(R.id.UserID);


			likeref = FirebaseDatabase.getInstance().getReference().child("Likes");
			likeref.keepSynced(true);
			commentnum = FirebaseDatabase.getInstance().getReference().child(COMMENT_KEY);
			commentnum.keepSynced(true);
			commentsnumber = itemView.findViewById(R.id.blog_comment_count);
			likesnum = (TextView) itemView.findViewById(R.id.blog_like_count);

			comment_seenref = FirebaseDatabase.getInstance().getReference().child("SeenPosts");
			comment_seenref.keepSynced(true);


		}




		private void downloadfile(String Link, String name) {

			FirebaseStorage storage = FirebaseStorage.getInstance();
			StorageReference storageRef = storage.getReferenceFromUrl(Link);

			final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Images");
			if (!rootPath.exists()) {
				rootPath.mkdirs();
			}

			final File localFile = new File(rootPath, name + ".jpg");
			storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
					Log.e("firebase ", ";local tem file created  created " + localFile.toString());

					if (localFile.canRead()) {


					}

					//Toast.makeText(context, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					Log.e("firebase ", ";local tem file not created  created " + exception.toString());
					Toast.makeText(context, "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
				}

			});
		}

	}


	public void downloadFile(String uRl,String name) {
		final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Images");

		if (!rootPath.exists()) {
			rootPath.mkdirs();
		}

		DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

		Uri downloadUri = Uri.parse(uRl);
		DownloadManager.Request request = new DownloadManager.Request(
				downloadUri);

		request.setAllowedNetworkTypes(
				DownloadManager.Request.NETWORK_WIFI
						| DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false).setTitle("تنزيل صوره")
				.setDescription("جاري تنزيل الصوره علي الهاتف")
				.setDestinationInExternalPublicDir("/FCAI Beni Suef/Images", name + ".jpg");

		mgr.enqueue(request);

	}




}
