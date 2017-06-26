package neu.cn.myflexboxlayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DEFAULT_WIDTH = "120";

    private static final String DEFAULT_HEIGHT = "80";

    private String ROW;

    private String COLUMN;

    private String ROW_REVERSE;

    private String COLUMN_REVERSE;

    private String NOWRAP;

    private String WRAP;

    private String WRAP_REVERSE;

    private String FLEX_START;

    private String FLEX_END;

    private String CENTER;

    private String BASELINE;

    private String STRETCH;

    private String SPACE_BETWEEN;

    private String SPACE_AROUND;

    private DrawerLayout mDrawerLayout;

    private FlexboxLayout mFlexboxLayout;

    private NavigationView mNavigationView;

    private FloatingActionButton addFloatingButton;

    private FloatingActionButton removeFloatingButton;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化各控件
        initView();
        initializeStringResources();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (mDrawerLayout != null) {
            mDrawerLayout.addDrawerListener(toggle);
        }
        toggle.syncState();
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            Menu mNavigationViewMenu = mNavigationView.getMenu();
            initializeFlexDirectionSpinner(mNavigationViewMenu);
            initializeFlexWrapSpinner(mNavigationViewMenu);
            initializeJustifyContentSpinner(mNavigationViewMenu);
            initializeAlignItemsSpinner(mNavigationViewMenu);
            initializeAlignContentSpinner(mNavigationViewMenu);
        }
        // 动态向FlexboxLayout中添加子View
        if (addFloatingButton != null) {
            addFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int viewIndex = mFlexboxLayout.getChildCount();
                    TextView textView = createBaseFlexItemTextView(viewIndex);
                    textView.setLayoutParams(createDefaultLayoutParams());
                    mFlexboxLayout.addView(textView);
                }
            });
        }
        // 动态移除FlexboxLayout中的子View
        if (removeFloatingButton != null) {
            removeFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFlexboxLayout.getChildCount() == 0) {
                        return;
                    }
                    mFlexboxLayout.removeViewAt(mFlexboxLayout.getChildCount() - 1);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "you clicked settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建TextView
     * @param index
     * @return
     */
    private TextView createBaseFlexItemTextView(int index) {
        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.flex_item_background);
        textView.setText(String.valueOf(index + 1));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private FlexboxLayout.LayoutParams createDefaultLayoutParams() {
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                Util.dpToPixel(this, readPreferenceAsInteger(getString(R.string.new_width_key), DEFAULT_WIDTH)),
                Util.dpToPixel(this, readPreferenceAsInteger(getString(R.string.new_height_key), DEFAULT_HEIGHT)));
        layoutParams.order = readPreferenceAsInteger(getString(R.string.new_flex_item_order_key), "1");
        layoutParams.flexGrow = readPreferenceAsFloat(getString(R.string.new_flex_grow_key), "0.0");
        layoutParams.flexShrink = readPreferenceAsFloat(getString(R.string.new_flex_shrink_key), "1.0");
        int flexBasisPercent = readPreferenceAsInteger(getString(R.string.new_flex_basis_percent_key), "-1");
        layoutParams.flexBasisPercent = flexBasisPercent == -1 ? -1 : (float) (flexBasisPercent / 100);
        return layoutParams;
    }

    private int readPreferenceAsInteger(String key, String defValue) {
        if (mSharedPreferences.contains(key)) {
            return Integer.valueOf(mSharedPreferences.getString(key, defValue));
        } else {
            return Integer.valueOf(defValue);
        }
    }

    private float readPreferenceAsFloat(String key, String defValue) {
        if (mSharedPreferences.contains(key)) {
            return Float.valueOf(mSharedPreferences.getString(key, defValue));
        } else {
            return Float.valueOf(defValue);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * 初始化下拉框
     *
     * @param currentValue
     * @param menuItemId
     * @param mNavigationViewMenu
     * @param arrayResourceId
     * @param listener
     */
    private void initializeSpinner(int currentValue, int menuItemId, Menu mNavigationViewMenu, int arrayResourceId,
                                   AdapterView.OnItemSelectedListener listener, ValueToStringConverter converter) {
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(mNavigationViewMenu.findItem(menuItemId));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResourceId, R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(listener);
        // 当前FlexboxLayout的getFlexDirection返回值为int类型转化为String类型数据
        String selectedAsString = converter.asString(currentValue);
        int position = adapter.getPosition(selectedAsString);
        spinner.setSelection(position);
    }

    /**
     * 初始化flexDirection下拉框，属性值有：row、row_reverse、column、column_reverse
     *
     * @param mNavigationViewMenu
     */
    private void initializeFlexDirectionSpinner(Menu mNavigationViewMenu) {
        initializeSpinner(mFlexboxLayout.getFlexDirection(), R.id.menu_item_flex_direction, mNavigationViewMenu, R.array.array_flex_direction,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int flexDirection = FlexboxLayout.FLEX_DIRECTION_ROW;
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals(ROW)) {
                            flexDirection = FlexboxLayout.FLEX_DIRECTION_ROW;
                        } else if (selected.equals(ROW_REVERSE)) {
                            flexDirection = FlexboxLayout.FLEX_DIRECTION_ROW_REVERSE;
                        } else if (selected.equals(COLUMN)) {
                            flexDirection = FlexboxLayout.FLEX_DIRECTION_COLUMN;
                        } else if (selected.equals(COLUMN_REVERSE)) {
                            flexDirection = FlexboxLayout.FLEX_DIRECTION_COLUMN_REVERSE;
                        }
                        mFlexboxLayout.setFlexDirection(flexDirection);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }, new ValueToStringConverter() {
                    @Override
                    public String asString(int value) {
                        switch (value) {
                            case FlexboxLayout.FLEX_DIRECTION_ROW:
                                return ROW;
                            case FlexboxLayout.FLEX_DIRECTION_ROW_REVERSE:
                                return ROW_REVERSE;
                            case FlexboxLayout.FLEX_DIRECTION_COLUMN:
                                return COLUMN;
                            case FlexboxLayout.FLEX_DIRECTION_COLUMN_REVERSE:
                                return COLUMN_REVERSE;
                            default:
                                return ROW;
                        }
                    }
                });
    }

    /**
     * 初始化flexWrap下拉框，属性值有：nowrap、wrap、wrap_reverse
     *
     * @param mNavigationViewMenu
     */
    private void initializeFlexWrapSpinner(Menu mNavigationViewMenu) {
        initializeSpinner(mFlexboxLayout.getFlexDirection(), R.id.menu_item_flex_wrap, mNavigationViewMenu, R.array.array_flex_wrap,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int flexWrap = FlexboxLayout.FLEX_WRAP_NOWRAP;
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals(NOWRAP)) {
                            flexWrap = FlexboxLayout.FLEX_WRAP_NOWRAP;
                        } else if (selected.equals(WRAP)) {
                            flexWrap = FlexboxLayout.FLEX_WRAP_WRAP;
                        } else if (selected.equals(WRAP_REVERSE)) {
                            flexWrap = FlexboxLayout.FLEX_WRAP_WRAP_REVERSE;
                        }
                        mFlexboxLayout.setFlexWrap(flexWrap);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }, new ValueToStringConverter() {
                    @Override
                    public String asString(int value) {
                        switch (value) {
                            case FlexboxLayout.FLEX_WRAP_NOWRAP:
                                return NOWRAP;
                            case FlexboxLayout.FLEX_WRAP_WRAP:
                                return WRAP;
                            case FlexboxLayout.FLEX_WRAP_WRAP_REVERSE:
                                return WRAP_REVERSE;
                            default:
                                return NOWRAP;
                        }
                    }
                });
    }

    /**
     * 初始化justifyContent下拉框，属性值有：flex_start、flex_end、center、space_between、space_around
     *
     * @param mNavigationViewMenu
     */
    private void initializeJustifyContentSpinner(Menu mNavigationViewMenu) {
        initializeSpinner(mFlexboxLayout.getFlexDirection(), R.id.menu_item_justify_content, mNavigationViewMenu, R.array.array_justify_content,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int justifyContent = FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals(FLEX_START)) {
                            justifyContent = FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
                        } else if (selected.equals(FLEX_END)) {
                            justifyContent = FlexboxLayout.JUSTIFY_CONTENT_FLEX_END;
                        } else if (selected.equals(CENTER)) {
                            justifyContent = FlexboxLayout.JUSTIFY_CONTENT_CENTER;
                        } else if (selected.equals(SPACE_BETWEEN)) {
                            justifyContent = FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN;
                        } else if (selected.equals(SPACE_AROUND)) {
                            justifyContent = FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND;
                        }
                        mFlexboxLayout.setJustifyContent(justifyContent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }, new ValueToStringConverter() {
                    @Override
                    public String asString(int value) {
                        switch (value) {
                            case FlexboxLayout.JUSTIFY_CONTENT_FLEX_START:
                                return FLEX_START;
                            case FlexboxLayout.JUSTIFY_CONTENT_FLEX_END:
                                return FLEX_END;
                            case FlexboxLayout.JUSTIFY_CONTENT_CENTER:
                                return CENTER;
                            case FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND:
                                return SPACE_AROUND;
                            case FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN:
                                return SPACE_BETWEEN;
                            default:
                                return FLEX_START;
                        }
                    }
                });
    }

    /**
     * 初始化alignItems下拉框，属性值有：stretch、flex_start、flex_end、center、baseline
     *
     * @param mNavigationViewMenu
     */
    private void initializeAlignItemsSpinner(Menu mNavigationViewMenu) {
        initializeSpinner(mFlexboxLayout.getFlexDirection(), R.id.menu_item_align_items, mNavigationViewMenu, R.array.array_align_items,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int alignItems = FlexboxLayout.ALIGN_ITEMS_STRETCH;
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals(FLEX_START)) {
                            alignItems = FlexboxLayout.ALIGN_ITEMS_FLEX_START;
                        } else if (selected.equals(FLEX_END)) {
                            alignItems = FlexboxLayout.ALIGN_ITEMS_FLEX_END;
                        } else if (selected.equals(CENTER)) {
                            alignItems = FlexboxLayout.ALIGN_ITEMS_CENTER;
                        } else if (selected.equals(BASELINE)) {
                            alignItems = FlexboxLayout.ALIGN_ITEMS_BASELINE;
                        } else if (selected.equals(STRETCH)) {
                            alignItems = FlexboxLayout.ALIGN_ITEMS_STRETCH;
                        }
                        mFlexboxLayout.setAlignItems(alignItems);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }, new ValueToStringConverter() {
                    @Override
                    public String asString(int value) {
                        switch (value) {
                            case FlexboxLayout.ALIGN_ITEMS_FLEX_START:
                                return FLEX_START;
                            case FlexboxLayout.ALIGN_ITEMS_FLEX_END:
                                return FLEX_END;
                            case FlexboxLayout.ALIGN_ITEMS_CENTER:
                                return CENTER;
                            case FlexboxLayout.ALIGN_ITEMS_BASELINE:
                                return BASELINE;
                            case FlexboxLayout.ALIGN_ITEMS_STRETCH:
                                return STRETCH;
                            default:
                                return STRETCH;
                        }
                    }
                });
    }

    /**
     * 初始化alignContent下拉框，属性值有：stretch、flex_start、flex_end、center、space_between、space_around
     *
     * @param mNavigationViewMenu
     */
    private void initializeAlignContentSpinner(Menu mNavigationViewMenu) {
        initializeSpinner(mFlexboxLayout.getFlexDirection(), R.id.menu_item_align_content, mNavigationViewMenu, R.array.array_align_content,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int alignContent = FlexboxLayout.ALIGN_CONTENT_STRETCH;
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals(FLEX_START)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_FLEX_START;
                        } else if (selected.equals(FLEX_END)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_FLEX_END;
                        } else if (selected.equals(CENTER)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_CENTER;
                        } else if (selected.equals(SPACE_BETWEEN)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_SPACE_BETWEEN;
                        } else if (selected.equals(SPACE_AROUND)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_SPACE_AROUND;
                        } else if (selected.equals(STRETCH)) {
                            alignContent = FlexboxLayout.ALIGN_CONTENT_STRETCH;
                        }
                        mFlexboxLayout.setAlignContent(alignContent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }, new ValueToStringConverter() {
                    @Override
                    public String asString(int value) {
                        switch (value) {
                            case FlexboxLayout.ALIGN_CONTENT_FLEX_START:
                                return FLEX_START;
                            case FlexboxLayout.ALIGN_CONTENT_FLEX_END:
                                return FLEX_END;
                            case FlexboxLayout.ALIGN_CONTENT_CENTER:
                                return CENTER;
                            case FlexboxLayout.ALIGN_CONTENT_SPACE_BETWEEN:
                                return SPACE_BETWEEN;
                            case FlexboxLayout.ALIGN_CONTENT_SPACE_AROUND:
                                return SPACE_AROUND;
                            case FlexboxLayout.ALIGN_CONTENT_STRETCH:
                                return STRETCH;
                            default:
                                return STRETCH;
                        }
                    }
                });
    }

    /**
     * int类型数据转化为String类型数据
     */
    private interface ValueToStringConverter {

        String asString(int value);
    }

    /**
     * 初始化字符串资源
     */
    private void initializeStringResources() {
        ROW = getString(R.string.row);
        COLUMN = getString(R.string.column);
        ROW_REVERSE = getString(R.string.row_reverse);
        COLUMN_REVERSE = getString(R.string.column_reverse);
        NOWRAP = getString(R.string.nowrap);
        WRAP = getString(R.string.wrap);
        WRAP_REVERSE = getString(R.string.wrap_reverse);
        FLEX_START = getString(R.string.flex_start);
        FLEX_END = getString(R.string.flex_end);
        CENTER = getString(R.string.center);
        BASELINE = getString(R.string.baseline);
        STRETCH = getString(R.string.stretch);
        SPACE_BETWEEN = getString(R.string.space_between);
        SPACE_AROUND = getString(R.string.space_around);
    }

    /**
     * 初始化各控件
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFlexboxLayout = (FlexboxLayout) findViewById(R.id.flexbox_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        addFloatingButton = (FloatingActionButton) findViewById(R.id.add_fab);
        removeFloatingButton = (FloatingActionButton) findViewById(R.id.remove_fab);
    }
}
