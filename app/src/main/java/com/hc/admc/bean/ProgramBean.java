package com.hc.admc.bean;

import java.util.List;

/**
 * Created by Alex on 2017/12/7.
 * 备注:
 */

public class ProgramBean {

    private List<ProgramListBean> programList;// 节目单节目列表

    public List<ProgramListBean> getProgramList() {
        return programList;
    }

    public void setProgramList(List<ProgramListBean> programList) {
        this.programList = programList;
    }

    public static class ProgramListBean {
        /**
         * layout : {"width":"1920","items":"[{\"id\": \"image_1_1509500651885\",\"x\": 0,\"y\": 0,\"width\": 32,\"height\": 18}, { \"id\": \"image_2_1509500716628\", \"x\": 0, \"y\": 0, \"width\": 4, \"height\": 4 }, { \"id\": \"image_3_1509500789706\", \"x\": 4, \"y\": 0, \"width\": 22, \"height\": 4 }, { \"id\": \"image_4_1509505053362\", \"x\": 26, \"y\": 0, \"width\": 6, \"height\": 4 } ] ","height":"1080"}
         * matItem : [{"itemId":"image_1_1509500651885","path":"20171110/f708757a-2345-4e61-a1c0-5c5bc252c8c1.png","fileSize":"0.01","sortNum":1,"type":1,"suffix":"png","matName":"节目单初稿.png"},{"itemId":"image_1_1509500651885","path":"20171101/6e89f9a5-f0b3-4fd4-b4f7-a4ea2e0f9845.jpg","fileSize":"0.01","sortNum":2,"type":1,"suffix":"jpg","matName":"猫.jpg"},{"itemId":"image_3_1509500789706","path":"20171101/5323cd8f-7aca-4d6e-a455-cd7975b0a68b.jpg","fileSize":"0.06","sortNum":1,"type":1,"suffix":"jpg","matName":"水.jpg"}]
         * playNum : 1
         * timing : {"beginDate":"2017-11-19","endDate":"2017-11-22","beginTime":"03:00:00","endTime":"00:00:00"}
         */

        private LayoutBean layout;// 节目信息
        private int playNum;// 节目播发顺序
        private TimingBean timing;// 节目播发时间
        private List<MatItemBean> matItem;// 素材列表

        public LayoutBean getLayout() {
            return layout;
        }

        public void setLayout(LayoutBean layout) {
            this.layout = layout;
        }

        public int getPlayNum() {
            return playNum;
        }

        public void setPlayNum(int playNum) {
            this.playNum = playNum;
        }

        public TimingBean getTiming() {
            return timing;
        }

        public void setTiming(TimingBean timing) {
            this.timing = timing;
        }

        public List<MatItemBean> getMatItem() {
            return matItem;
        }

        public void setMatItem(List<MatItemBean> matItem) {
            this.matItem = matItem;
        }

        public static class LayoutBean {
            /**
             * width : 1920
             * items : [{"id": "image_1_1509500651885","x": 0,"y": 0,"width": 32,"height": 18}, { "id": "image_2_1509500716628", "x": 0, "y": 0, "width": 4, "height": 4 }, { "id": "image_3_1509500789706", "x": 4, "y": 0, "width": 22, "height": 4 }, { "id": "image_4_1509505053362", "x": 26, "y": 0, "width": 6, "height": 4 } ]
             * height : 1080
             */

            private String width;// 节目主屏宽度
            private String items;// 节目布局块信息
            private String height;//节目主屏高度

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getItems() {
                return items;
            }

            public void setItems(String items) {
                this.items = items;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }
        }

        //控制节目单播放时间
        public static class TimingBean {
            /**
             * beginDate : 2017-11-19 开始日期
             * endDate : 2017-11-22 结束日期
             * beginTime : 03:00:00 每天开始时间
             * endTime : 00:00:00  每天结束时间
             */

            private String beginDate;
            private String endDate;
            private String beginTime;
            private String endTime;

            public String getBeginDate() {
                return beginDate;
            }

            public void setBeginDate(String beginDate) {
                this.beginDate = beginDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }
        }

        public static class MatItemBean {
            /**
             * itemId : image_1_1509500651885
             * path : 20171110/f708757a-2345-4e61-a1c0-5c5bc252c8c1.png
             * fileSize : 0.01
             * sortNum : 1
             * type : 1
             * suffix : png
             * matName : 节目单初稿.png
             */

            private String itemId;// 节目区块标识  video_3_1511748600792  如果显示区块是重叠的话   video_{{z-index}}_1511748600792   z-index 这个值越大，表示显示越顶层
            private String path;// 素材地址
            private String fileSize;// 素材大小
            private int sortNum;// 素材播放顺序
            private int type;// 素材类型：  //1:图片，2:视频，3:音频，4:文字,5:网址
            private String suffix;// 素材文件后缀
            private String matName;// 素材原名称

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getFileSize() {
                return fileSize;
            }

            public void setFileSize(String fileSize) {
                this.fileSize = fileSize;
            }

            public int getSortNum() {
                return sortNum;
            }

            public void setSortNum(int sortNum) {
                this.sortNum = sortNum;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public String getMatName() {
                return matName;
            }

            public void setMatName(String matName) {
                this.matName = matName;
            }
        }
    }


}
