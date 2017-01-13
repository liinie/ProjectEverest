package com.linxu.mounteverest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lin xu on 15.12.2016.
 */

public class CustomSliderView extends View{
    private Rect slider;
    private Paint sliderPaint;
    private GestureDetector gestureDetector;

    private List<Rect> eventMarkers;
    private Rect draggedMarker = null;
    private final int eventMarkerHeight = 80;
    private Paint eventPaint;

    private Rect startDateRegion;
    private Rect endDateRegion;
    private Paint dateRegionPaint;

    private DatePickerDialog.OnDateSetListener onDateSetListener;


    private AddProject addProject;

    public CustomSliderView(Context context) {
        super(context);
        init();
    }

    public CustomSliderView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }

    public void init(){
        int x = 100;
        int y = 150;
        int sideWidth = 200;
        int sideHeight = 1300;

        // create a slider that we'll draw later
        slider = new Rect(x, y, x+sideWidth, y+sideHeight);

        eventMarkers = new ArrayList<>();

        // create the Paint and set its color
        sliderPaint = new Paint();
        sliderPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        eventPaint = new Paint();
        eventPaint.setColor(Color.BLACK);

        startDateRegion = new Rect(slider.left, slider.top - 100, slider.right, slider.top);
        dateRegionPaint = new Paint();
        dateRegionPaint.setColor(Color.CYAN);
        //gestureDetector = new GestureDetector(getContext(), new mListener());

        endDateRegion = new Rect(slider.left, slider.bottom, slider.right, slider.bottom + 100);

        onDateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar.getInstance().set(Calendar.YEAR, year);
                Calendar.getInstance().set(Calendar.MONTH, month);
                Calendar.getInstance().set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int eventAction = event.getAction();

        int x = (int)event.getX();
        int y = (int)event.getY();

        boolean checkStartDate;

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                //double heightFraction = (double)(y - slider.top) / slider.height();
                //Toast.makeText(getContext(), "down", Toast.LENGTH_SHORT).show();
                if (startDateRegion.contains(x, y)) {
                    checkStartDate = true;
                    openDatePickerDialog(onDateSetListener, checkStartDate);
                    break;
                }

                if(endDateRegion.contains(x, y)){
                    checkStartDate = false;
                    openDatePickerDialog(onDateSetListener, checkStartDate);
                    break;
                }

                if (slider.contains(x, y)) {
                    for (Rect eventMarker : eventMarkers) {
                        if (eventMarker.contains(x, y)) {
                            draggedMarker = eventMarker;
                        }
                    }

                    if (draggedMarker == null) {
                        eventMarkers.add(new Rect(slider.left, y - eventMarkerHeight / 2,
                                slider.right, y + eventMarkerHeight / 2));
                        draggedMarker = eventMarkers.get(eventMarkers.size() - 1);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                draggedMarker = null;

                break;

            case MotionEvent.ACTION_MOVE:
                if (draggedMarker != null) {
                    draggedMarker.top = y - eventMarkerHeight / 2;
                    draggedMarker.bottom = y + eventMarkerHeight / 2;
                }
                break;
        }

        // tell the View to redraw the Canvas
        invalidate();
//
        // tell the View that we handled the event
        return true;
//
    }

    private class mListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            Toast.makeText(getContext(), "gesture detector down", Toast.LENGTH_SHORT).show();
            return super.onDown(e);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(slider, sliderPaint);

        for (Rect eventMarker : eventMarkers) {
            canvas.drawRect(eventMarker, eventPaint);
        }

        canvas.drawRect(startDateRegion, dateRegionPaint);
        canvas.drawRect(endDateRegion, dateRegionPaint);

    }

    private void openDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener, final Boolean checkStartDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String myFormat = "dd/MM/yy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    String newTime = sdf.format(calendar.getTime());
                    if(checkStartDate) {
                        //Log.d("choosed Start Date", newTime);
                        addProject.changeStartDate(newTime);
                    }else{
                        //Log.d("choosed End Date", newTime);
                        addProject.changeEndDate(newTime);
                    }

                }
            }
        });
        dialog.show();
    }

    public void register(AddProject project) {
        this.addProject = project;
    }
}
