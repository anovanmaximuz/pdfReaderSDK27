package com.novel.android.dilan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This fragment has a big {@ImageView} that shows PDF pages, and 2
 * {@link android.widget.Button}s to move between pages. We use a
 * {@link android.graphics.pdf.PdfRenderer} to render PDF pages as
 * {@link android.graphics.Bitmap}s.
 */
public class PdfRendererBasicFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener {

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * The filename of the PDF.
     */
    private static String FILENAME = "dilan_2.pdf";

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link android.widget.ImageView} that shows a PDF page as a {@link android.graphics.Bitmap}
     */
    private ImageView mImageView;

    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button mButtonPrevious;

    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button mButtonNext;

    /**
     * PDF page index
     */
    private int mPageIndex;


    //Animation
    private Animation animFadeIn;
    private Animation animFadeOut;

    // Position
    private int _xDelta;
    private int _yDelta;

    private AdView mAdView;
    public PdfRendererBasicFragment() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String novel = bundle.getString("novel", "dilan_1.pdf");
            FILENAME = novel;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        mImageView = (ImageView) view.findViewById(R.id.image);
        mButtonPrevious = (Button) view.findViewById(R.id.previous);
        mButtonNext = (Button) view.findViewById(R.id.next);

        MobileAds.initialize(getContext(), "ca-app-pub-6793022751700770~7532674547");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Animation
        animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_in);
        animFadeIn.setAnimationListener(this);
        animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_out);
        // Bind events.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);

        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                mAdView.setVisibility(View.VISIBLE);
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        if (animation == animFadeIn) {
            mImageView.startAnimation(animFadeOut);
            mImageView.startAnimation(animFadeIn);
        }
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationStart(Animation arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getContext());
            showPage(mPageIndex);

            mImageView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
                public void onSwipeTop() {
                    // Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeRight() {
                    //Toast.makeText(getContext(), "right", Toast.LENGTH_SHORT).show();
                    if (mCurrentPage.getIndex() > 0)
                        showPage(mCurrentPage.getIndex() - 1);
                }

                public void onSwipeLeft() {
                    //Toast.makeText(getContext(), "left", Toast.LENGTH_SHORT).show();
                    if (mCurrentPage.getIndex() < mPdfRenderer.getPageCount())
                        showPage(mCurrentPage.getIndex() + 1);
                }

                public void onSwipeBottom() {
                    //Toast.makeText(getContext(), "bottom", Toast.LENGTH_SHORT).show();
                }

            });

            /*
            mImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((MainActivity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    final int X = (int) event.getRawX();
                    final int Y = (int) event.getRawY();
                    Log.e("XY","X:"+String.valueOf(X)+"-Y"+String.valueOf(Y)+"---"+String.valueOf(height)+"-"+String.valueOf(width));
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return true;
                }
            });
            */

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(FILENAME);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    /**
     * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        mImageView.startAnimation(animFadeOut);
        mImageView.setImageBitmap(bitmap);

        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        if(FILENAME.equals("dilan_1.pdf")){
            ((MainActivity)getActivity()).setTitleToolbar(getString(R.string.dilan_1990));
        }else if(FILENAME.equals("dilan_2.pdf")){
            ((MainActivity)getActivity()).setTitleToolbar(getString(R.string.dilan_1991));
        }else if(FILENAME.equals("dilan_3.pdf")){
            ((MainActivity)getActivity()).setTitleToolbar(getString(R.string.dilan_milea));
        }

        ((MainActivity)getActivity()).setPage(getString(R.string.page_selector, index + 1, pageCount));
        Log.e("Page"," Hasil: "+String.valueOf(index % 30));
        if(index % 30 == 0 ) {
            //showBigAdd();
        }
        //getActivity().setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

}
