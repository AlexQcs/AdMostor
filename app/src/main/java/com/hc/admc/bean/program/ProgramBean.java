package com.hc.admc.bean.program;

import java.util.List;

/**
 * Created by Alex on 2017/12/20.
 * 备注:
 */

public class ProgramBean {

    private List<ProgramListBean> programList;

    public List<ProgramListBean> getProgramList() {
        return programList;
    }

    public void setProgramList(List<ProgramListBean> programList) {
        this.programList = programList;
    }

    public static class ProgramListBean {
        /**
         * layout : {"width":"1920","items":[{"x":0,"width":12,"y":0,"id":"video_3_1511748600792","reality_width":"360.00","reality_height":"540.00","height":18},{"x":12,"width":20,"y":0,"id":"image_2_1511748944557","reality_width":"600.00","reality_height":"540.00","height":18}],"height":"1080"}
         * matItem : [{"itemId":"video_3_1511748600792","path":"20171127/603071d4-85fc-4731-a6ee-90f7fd1952d2.mp4","fileSize":"13.28","sortNum":1,"type":2,"suffix":"mp4","matName":"xs.mp4"},{"itemId":"video_3_1511748600792","path":"20171108/36e10916-659e-4fa0-a516-1ac11a430b2d.mp4","fileSize":"2.38","sortNum":2,"type":2,"suffix":"mp4","matName":"测试视频.mp4"},{"itemId":"image_2_1511748944557","path":"20171110/f708757a-2345-4e61-a1c0-5c5bc252c8c1.png","fileSize":"0.01","sortNum":1,"type":1,"suffix":"png","matName":"节目单初稿.png"},{"itemId":"image_2_1511748944557","path":"20171101/6e89f9a5-f0b3-4fd4-b4f7-a4ea2e0f9845.jpg","fileSize":"0.01","sortNum":2,"type":1,"suffix":"jpg","matName":"猫.jpg"}]
         * resource : ["20171110/f708757a-2345-4e61-a1c0-5c5bc252c8c1.png","20171108/36e10916-659e-4fa0-a516-1ac11a430b2d.mp4","20171101/6e89f9a5-f0b3-4fd4-b4f7-a4ea2e0f9845.jpg","20171127/603071d4-85fc-4731-a6ee-90f7fd1952d2.mp4"]
         * playNum : 1
         * timing : {"beginDate":"2017-11-19","endDate":"2017-11-22","beginTime":"03:00:00","endTime":"00:00:00"}
         * domain : http://127.0.0.1/webfile/
         */

        private LayoutBean layout;// 节目信息
        private int playNum;// 节目播发顺序
        private TimingBean timing;// 节目播发时间
        private List<MatItemBean> matItem;// 素材列表
        private String domain;//资源下载服务器地址
        private List<String> resource;//资源地址列表

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

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public List<MatItemBean> getMatItem() {
            return matItem;
        }

        public void setMatItem(List<MatItemBean> matItem) {
            this.matItem = matItem;
        }

        public List<String> getResource() {
            return resource;
        }

        public void setResource(List<String> resource) {
            this.resource = resource;
        }

        public static class LayoutBean {
            /**
             * width : 1920
             * items : [{"x":0,"width":12,"y":0,"id":"video_3_1511748600792","reality_width":"360.00","reality_height":"540.00","height":18},{"x":12,"width":20,"y":0,"id":"image_2_1511748944557","reality_width":"600.00","reality_height":"540.00","height":18}]
             * height : 1080
             */

            private String width;// 节目主屏宽度
            private String height;//节目主屏高度
            private List<ItemsBean> items;// 节目布局块列表

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public List<ItemsBean> getItems() {
                return items;
            }

            public void setItems(List<ItemsBean> items) {
                this.items = items;
            }

            public static class ItemsBean {
                /**
                 * x : 0
                 * width : 12
                 * y : 0
                 * id : video_3_1511748600792
                 * reality_width : 360.00
                 * reality_height : 540.00
                 * height : 18
                 */

                private int x;
                private int width;
                private int y;
                private String id;
                private String reality_width;
                private String reality_height;
                private int height;

                private int reality_x;
                private int reality_y;

                public int getX() {
                    return x;
                }

                public void setX(int x) {
                    this.x = x;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getY() {
                    return y;
                }

                public void setY(int y) {
                    this.y = y;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getReality_width() {
                    return reality_width;
                }

                public void setReality_width(String reality_width) {
                    this.reality_width = reality_width;
                }

                public String getReality_height() {
                    return reality_height;
                }

                public void setReality_height(String reality_height) {
                    this.reality_height = reality_height;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getReality_x() {
                    return reality_x;
                }

                public void setReality_x(int reality_x) {
                    this.reality_x = reality_x;
                }

                public int getReality_y() {
                    return reality_y;
                }

                public void setReality_y(int reality_y) {
                    this.reality_y = reality_y;
                }
            }

            @Override
            public String toString() {
                return "LayoutBean{" +
                        "width='" + width + '\'' +
                        ", height='" + height + '\'' +
                        ", items=" + items +
                        '}';
            }
        }

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

            @Override
            public String toString() {
                return "TimingBean{" +
                        "beginDate='" + beginDate + '\'' +
                        ", endDate='" + endDate + '\'' +
                        ", beginTime='" + beginTime + '\'' +
                        ", endTime='" + endTime + '\'' +
                        '}';
            }
        }

        public static class MatItemBean {
            /**
             * itemId : video_3_1511748600792
             * path : 20171127/603071d4-85fc-4731-a6ee-90f7fd1952d2.mp4
             * fileSize : 13.28
             * sortNum : 1
             * type : 1         1:图片 2:视频 3:音频 4:文字 5:网址
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

            @Override
            public String toString() {
                return "MatItemBean{" +
                        "itemId='" + itemId + '\'' +
                        ", path='" + path + '\'' +
                        ", fileSize='" + fileSize + '\'' +
                        ", sortNum=" + sortNum +
                        ", type=" + type +
                        ", suffix='" + suffix + '\'' +
                        ", matName='" + matName + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ProgramListBean{" +
                    "layout=" + layout +
                    ", playNum=" + playNum +
                    ", timing=" + timing +
                    ", matItem=" + matItem +
                    ", domain='" + domain + '\'' +
                    ", resource=" + resource +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProgramBean{" +
                "programList=" + programList +
                '}';
    }
}
