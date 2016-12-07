# TimePicker
时间与日期选择控件

# Android 自定义时间滑轮选择控件
刚开始项目中使用原生的控件，5.0的控件可以说很炫酷，很漂亮，很是喜欢，不过客户反应用不来，太麻烦~，好吧原谅你一次~。
所以为了客户的着想使用4.0的那种时间选择控件，可是大家都懂的，控件简单暴力，就是太难看了，呜呜~
so~ 为了给整个APP搭配，选择以4.0的控件为基础进行改进自定义，让它美美哒~
下面大家可以看图对比下。

这个是原生的图

![原生](http://i.imgur.com/Ya3QV5k.png)

经过改进美化的图，而且可以自定义标题~

![](https://github.com/ssyandroid/TimePicker/raw/master/time.png)

![](https://github.com/ssyandroid/TimePicker/raw/master/date.png)

是不是好看多了，并且支持自定义！
也就是说这个控件只是一个继承AlertDialog自定义的。
主要的滑轮还是使用的是NumberPicker，用了它基本上都能满足自定义的滑轮选择控件。
不过要注意的是，直接采用原生的NumberPicker可能无法达到效果，因为原生的直接改动很小，所以我们是直接把原生的代码拷贝一份，重新自定义自己的NumberPicker，只需改动一点点议即可~

直接进入主题：

>- 使用原生的NumberPicker自定义自己需要的NumberPicker

>- 实现MyTimePickerDialog继承AlertDialog进行自定义

>- 代码使用，使用方法当然要给原生的差不多啦~

首先使用原生的NumberPicker自定义自己需要的NumberPicker，大家可以在API源代码了找到NumberPicker类，进行复制粘贴到自己项目工程里。
当然要找API23以下的，我看25的已经变化了，在android.widget里很容易找到的~，找不到源码的自行谷歌哈，当然，亦可以使用我的，全代码在后面会备上的~
直接复制过来，肯定有的方法会报错，因为找不到方法，那也很简单，一是看看该方法是什么作用，重要不重要，如果说的该方法可以不要对项目无影响，则进行直接处理错误，如果有用，可以找个替代的方法补替即可。
同理，大家可以根据自己的需求自己写一个，或者谷歌一个适合自己的。适合自己的才是最好的，大家灵活变通啦~
		
但是要注意我们要写上它的属性配置，在你的attrs文件里写上，大家可以找源代码里的属性，我这里直接谷歌的：
		<!-- Don't touch this -->
		<attr name="numberPickerStyle" format="reference" />
		
		<declare-styleable name="NumberPicker">
		    <!-- @hide Color for the solid color background if such for optimized rendering. -->
		    <attr name="solidColor" format="color|reference" />
		    <!-- @hide The divider for making the selection area. -->
		    <attr name="selectionDivider" format="reference" />
		    <!-- @hide The height of the selection divider. -->
		    <attr name="selectionDividerHeight" format="dimension" />
		    <!-- @hide The distance between the two selection dividers. -->
		    <attr name="selectionDividersDistance" format="dimension" />
		    <!-- @hide The min height of the NumberPicker. -->
		    <attr name="internalMinHeight" format="dimension" />
		    <!-- @hide The max height of the NumberPicker. -->
		    <attr name="internalMaxHeight" format="dimension" />
		    <!-- @hide The min width of the NumberPicker. -->
		    <attr name="internalMinWidth" format="dimension" />
		    <!-- @hide The max width of the NumberPicker. -->
		    <attr name="internalMaxWidth" format="dimension" />
		    <!-- @hide The layout of the number picker. -->
		    <attr name="internalLayout" format="reference" />
		    <!-- @hide The drawable for pressed virtual (increment/decrement) buttons. -->
		    <attr name="virtualButtonPressedDrawable" format="reference" />
		</declare-styleable>
然后还要在主题文件里设置这些属性以满足自己需求哦。下面是我的仅供参考：

		  	<style name="NPWidget">
		    <item name="android:textAppearance">?android:attr/textAppearance</item>
		    </style>
		
		    <style name="NPWidget.NumberPicker">
		        <item name="android:orientation">vertical</item>
		        <item name="android:fadingEdge">vertical</item>
		        <item name="android:fadingEdgeLength">50dip</item>
		    </style>
		
		    <!--NumberPicker style-->
		    <style name="NPWidget.Holo.NumberPicker" parent="NPWidget.NumberPicker">
		        <item name="solidColor">@android:color/transparent</item>
		        <item name="selectionDivider">@color/actionbarcolor_press</item>
		        <item name="selectionDividerHeight">2dip</item>
		        <item name="internalLayout">@layout/number_picker_with_selector_wheel</item>
		        <item name="internalMinWidth">50dip</item>
		        <item name="internalMaxHeight">150dip</item>
		        <!--<item name="virtualButtonPressedDrawable">@drawable/item_background_holo_dark</item>-->
		    </style>

这样大家就完成自己的NumberPicker，然后需要到NumberPicker都已直接使用的~
比如直接xml里使用：

		<com.sansan.widget.NumberPicker
		        android:id="@+id/minute"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        android:focusable="true"
		        android:focusableInTouchMode="true" />


这就完成了第一步。

### 第二步 实现MyTimePickerDialog继承AlertDialog进行自定义 ###
这个大家都会，和简单，主要就是自定义布局，与实现自己业务与需求。
在此就简单叙述下，大家先看下我的：

	 	/**
	     * Creates a new time picker dialog with the specified theme.
	     *
	     * @param context   the parent context
	     * @param listener  the listener to call when the time is set
	     * @param dateTime  时间比如12:01，一定要这个格式，如果为空则为默认当前时间
	     * @param hourOfDay the initial hour
	     * @param minute    the initial minute
	     */
	    private MyTimePickerDialog(Context context, String title, OnTimeSetListener listener, String dateTime, int hourOfDay, int minute) {
	        super(context);
			//设置对话框标题，没有就默认
	        if (!TextUtils.isEmpty(title)) {
	            setTitle(title);
	        }
	
	        mTimeSetListener = listener;
	
			//这个是显示自己设置的时间，没有就是默认当前时间
	        if (hourOfDay < 0 || minute < 0) {
	            if (TextUtils.isEmpty(dateTime)) {
	                //初始化时间
	                Calendar calendar = Calendar.getInstance();
	                calendar.setTimeInMillis(System.currentTimeMillis());
	                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
	                minute = calendar.get(Calendar.MINUTE);
	            } else {
	                try {
	                    hourOfDay = Integer.valueOf(dateTime.substring(0, 2));
	                    minute = Integer.valueOf(dateTime.substring(3, 5));
	                } catch (NumberFormatException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	
	        final Context themeContext = getContext();
			//加载自定义布局
	        final LayoutInflater inflater = LayoutInflater.from(themeContext);
	        view = inflater.inflate(R.layout.time_picker_dialog, null);
	        setView(view);
	        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), this);
	        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this);
	
	        mHour = (NumberPicker) view.findViewById(R.id.hour);
	        mMinute = (NumberPicker) view.findViewById(R.id.minute);
	
			//使用NumberPicker里的方法设置最大，最小值
	        mHour.setMaxValue(23);
	        mHour.setMinValue(0);
	        mHour.setFocusable(true);
	        mHour.setFocusableInTouchMode(true);
	        mHour.setFormatter(this);
	        mHour.setValue(hourOfDay);
	        mHour.setOnValueChangedListener(this);
	
	        mMinute.setMaxValue(59);
	        mMinute.setMinValue(0);
	        //设置分别长按向上和向下按钮时数字增加和减少的速度。默认值为300 ms
	        mMinute.setOnLongPressUpdateInterval(100);
	        mMinute.setFocusable(true);
	        mMinute.setFocusableInTouchMode(true);
	        mMinute.setFormatter(this);
	        mMinute.setValue(minute);
	        mMinute.setOnValueChangedListener(this);
	
	    }


上面的方法都是简单常用的方法，这样就可以简单实现滑轮了，当然如果做时间用的话，还是需要考虑，从59滑动到0的时候要需要考虑时增加一位，从0到59当然也需要减一位。从上面代码可以看出我们实现了setOnValueChangedListener监听事件，所以我们实现该事件即可：

		@Override
	    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
	        if (picker == mHour) {
	            //时发生变化时候，分不变
	        } else if (picker == mMinute) {
	            //分发生变化时候，如果是从最大到最小，时加一，最小到最大减一
	            int minValue = mMinute.getMinValue();
	            int maxValue = mMinute.getMaxValue();
	            int newHour = mHour.getValue();
	            if (oldVal == maxValue && newVal == minValue) {
	                newHour = newHour + 1;
	            } else if (oldVal == minValue && newVal == maxValue) {
	                newHour = newHour - 1;
	            }
	            setValue(mHour, newHour);
	        }
	    }
	
	    /**
	     * 设置值，并且进行值的比较，如果值小于最小取最小，值大于最大取最大
	     *
	     * @param picker  NumberPicker
	     * @param current 当前值
	     */
	    public void setValue(NumberPicker picker, int current) {
	        if (picker.getValue() == current) {
	            return;
	        }
	        current = Math.max(current, picker.getMinValue());
	        current = Math.min(current, picker.getMaxValue());
	        picker.setValue(current);
	    }

该方法即可简单实现啦~
剩下的就是监听对话框的确定与取消事件赛：

		@Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which) {
	            case BUTTON_POSITIVE:
	                if (mTimeSetListener != null) {
	                    int hour = mHour.getValue();
	                    int minute = mMinute.getValue();
	                    String timeStr = format(hour) + ":" + format(minute);
	                    mTimeSetListener.onTimeSet(view, timeStr, hour, minute);
	                }
	                break;
	            case BUTTON_NEGATIVE:
	                cancel();
	                break;
	        }
	    }

上面方法都比较简单，就不在多说了，format方法就是让个位数前面加上0，方法有很多，我用的笨方法：

		@Override
	    public String format(int value) {
	        String tmpStr = String.valueOf(value);
	        if (value < 10) {
	            tmpStr = "0" + tmpStr;
	        }
	        return tmpStr;
	    }

然后就是布局，话不多说，看代码，也给大家看看吧：

		<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    android:layout_width="wrap_content"
		    android:layout_height="wrap_content"
		    android:layout_gravity="center_horizontal"
		    android:gravity="center"
		    android:orientation="horizontal">
		    <!-- hour -->
		    <com.sansan.widget.NumberPicker
		        android:id="@+id/hour"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        android:focusable="true"
		        android:focusableInTouchMode="true" />
		
		    <!-- divider -->
		    <TextView
		        android:id="@+id/divider"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        android:layout_gravity="center_vertical"
		        android:padding="10dp"
		        android:text="@string/time_picker_hour_hint"
		        android:textColor="@color/colors" />
		
		    <!-- minute -->
		    <com.sansan.widget.NumberPicker
		        android:id="@+id/minute"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        android:focusable="true"
		        android:focusableInTouchMode="true" />
		    <!-- divider -->
		    <TextView
		        android:id="@+id/divider2"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        android:layout_gravity="center_vertical"
		        android:padding="10dp"
		        android:text="@string/time_picker_minute_hint"
		        android:textColor="@color/colors" />
		</LinearLayout>

到此基本解决~，要注意要继承v7包的android.support.v7.app.AlertDialog，不然达不到效果别怪我哦~

### 另外项目里已经附上日期的选择框，方法同理时间 ###
