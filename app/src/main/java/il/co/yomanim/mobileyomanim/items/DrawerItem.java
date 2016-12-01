package il.co.yomanim.mobileyomanim.items;

public class DrawerItem {
    private String ItemName;
    private int ImgResId;

    public DrawerItem(String name, int icon) {
        this.ItemName = name;
        this.ImgResId = icon;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getImgResId() {
        return ImgResId;
    }

    public void setImgResId(int imgResId) {
        ImgResId = imgResId;
    }
}