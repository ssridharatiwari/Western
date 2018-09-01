package com.milk.milkcollection.model;


public class LossReport {
    String weight="",amount="",date="",count="",shift="",sellwt="0",sellAmout="0",lossProfitAmount="0";

    Boolean isLoss = true;
    public LossReport(){}

    public void setBuydataData(String weight,String date,String count,String amount,String sift) {
        this.date = date;
        this.weight = weight;
        this.count = count;
        this.amount = amount;
        this.shift = sift;
    }

    public void setSellData(String weight,String amount) {
        this.sellwt = weight;
        this.sellAmout = amount;

    }

    public void setIsLoss(Boolean isLoss) {
    this.isLoss = isLoss;
}

    public String getLoss() {

       if (this.isLoss)
           return "Loss";
       else return "Profit";
    }

    public String getLossProfitAmount() { return lossProfitAmount;}
    public void setlossProfitAmount(String amount) {this.lossProfitAmount = amount;}



    public String getSellwt() { return sellwt;}
    public String getSellAmout() { return sellAmout;}


    public String getShift() { return shift;}

    public String getCount() { return count;}

    public void setCount(String count) {this.count = count;}

    public String getDate() { return date;}

    public void setDate(String date) {this.date = date;}

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
