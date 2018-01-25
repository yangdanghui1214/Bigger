package cn.tsign.datadisplay.lib.region.bean;

import android.graphics.Path;
import android.graphics.Region;

import cn.tsign.datadisplay.lib.region.utils.CommonUtil;


/**
 * 地图信息
 *
 * @author haru
 * @date 2018/1/12
 */

public class MapPathInfo {

    public MapPathInfo(Path path) {
        this.path = path;
        region = CommonUtil.genRegion(path);
    }

    /**
     * 路径区域
     */
    public Region region;
    /**
     * 区域路径
     */
    public Path path;
    /**
     * 是否激活，激活的才能被选中高亮显示
     */
    public boolean isActivated = false;
    /**
     * 激活颜色，优先级最高，-1表示未设置，采用默认
     */
    public int activatedColor = -1;
    /**
     * 高亮颜色，优先级最高，-1表示未设置，采用默认
     */
    public int highlightColor = -1;
    /**
     * 正常颜色，优先级最高，-1表示未设置，采用默认
     */
    public int normalColor = -1;
}
