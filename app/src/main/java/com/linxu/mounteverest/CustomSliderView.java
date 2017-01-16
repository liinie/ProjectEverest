package com.linxu.mounteverest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lin xu on 15.12.2016.
 */

public class CustomSliderView extends View {
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

    private AddProject addProject;
    private boolean isOkayClicked;

    private int yearStr;
    private int monthStr;
    private int dayStr;

    private String startDate = "";
    private String endDate = "";

    private boolean checkStartDate;
    private Long startDateInMillis;
    private Long endDateInMillis;

    private float dayCount = 0f;

    public CustomSliderView(Context context) {
        super(context);
        init();
    }

    public CustomSliderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public void init() {
        int x = 100;
        int y = 150;
        int sideWidth = 200;
        int sideHeight = 1300;

        // create a slider that we'll draw later
        slider = new Rect(x, y, x + sideWidth, y + sideHeight);

        eventMarkers = new ArrayList<>();

        // create the Paint and set its color
        sliderPaint = new Paint();
        sliderPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        eventPaint = new Paint();
        eventPaint.setColor(Color.BLACK);

        startDateRegion = new Rect(slider.left, slider.top - 100, slider.right, slider.top);
        dateRegionPaint = new Paint();
        dateRegionPaint.setColor(Color.CYAN);

        endDateRegion = new Rect(slider.left, slider.bottom, slider.right, slider.bottom + 100);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:

                if (startDateRegion.contains(x, y)) {
                    checkStartDate = true;
                    showDatePicker();
                    break;
                }

                if (endDateRegion.contains(x, y)) {
                    checkStartDate = false;
                    showDatePicker();
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

        // tell the View that we handled the event
        return true;
    }


    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();

        yearStr = c.get(Calendar.YEAR);
        monthStr = c.get(Calendar.MONTH);
        dayStr = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                if (isOkayClicked) {
                    if (checkStartDate) {
                        startDate = Integer.toString(selectedYear) + "/0" + Integer.toString(selectedMonth + 1) + "/" + Integer.toString(selectedDay);
                        addProject.changeStartDate(startDate);
                    } else {
                        endDate = Integer.toString(selectedYear) + "/0" + Integer.toString(selectedMonth + 1) + "/" + Integer.toString(selectedDay);
                        addProject.changeEndDate(endDate);
                    }
                    yearStr = selectedYear;
                    monthStr = selectedMonth;
                    dayStr = selectedDay;

                }
                isOkayClicked = false;
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), datePickerListener,
                yearStr, monthStr, dayStr);

        if (checkStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            Log.d("currentTimeMillis: ", Long.toString(System.currentTimeMillis()));


        } else {
            datePickerDialog.getDatePicker().setMinDate(startDateInMillis);
            //Log.d("start date", Long.toString(startDateInMillis));
        }

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.cancel();
                            isOkayClicked = false;
                        }
                    }
                });

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            isOkayClicked = true;
                            DatePicker datePicker = datePickerDialog.getDatePicker();
                            datePickerListener.onDateSet(datePicker,
                                    datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth());
                            c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            c.set(Calendar.MONTH, datePicker.getMonth());
                            c.set(Calendar.YEAR, datePicker.getYear());

                            if(checkStartDate){
                                startDateInMillis = c.getTimeInMillis();
                                Log.d("start date after ok", Long.toString(startDateInMillis));
                            }else{
                                endDateInMillis = c.getTimeInMillis();
                                Log.d("end date after ok", Long.toString(endDateInMillis));
                            }

                            if(addProject.isEndDateSet() && addProject.isStartDateSet()){
                                discreteSlider();
                            }

                        }
                    }
                });

        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void discreteSlider() {
        //todo: discrete the slide.
        dayCount = (endDateInMillis - startDateInMillis)/(1000 * 60 * 60 * 24);
        int days = (int)dayCount + 1;
        Log.d("days ", Integer.toString(days));

    }


    private class mListener extends GestureDetector.SimpleOnGestureListener {
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

    public void register(AddProject project) {
        this.addProject = project;
    }
}
