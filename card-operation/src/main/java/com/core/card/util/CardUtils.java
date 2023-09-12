package com.core.card.util;

import com.core.common.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CardUtils {


    public static Boolean validateBankCardNumber(String bankCardNumber) {
        try {
            if(bankCardNumber==null)
               return false;

            int L = bankCardNumber.length();
            if (L < 16 || Long.parseLong(bankCardNumber.substring(1, 11)) == 0 || Long.parseLong(bankCardNumber.substring(10, 16)) == 0)
                return false;

            int c = Integer.parseInt(bankCardNumber.substring(15, 16));
            int s = 0;
            int k, d;
            for (int i = 0; i < 16; i++) {
                k = (i % 2 == 0) ? 2 : 1;
                d = Integer.parseInt(bankCardNumber.substring(i, i + 1)) * k;
                s += (d > 9) ? d - 9 : d;
            }
            return ((s % 10) == 0);
        }catch (Exception ex){
            return false;
        }
    }


    public static Boolean checkBankCardName(String bankCardNumber) {

        if (!validateBankCardNumber(bankCardNumber)) return false;
        String number = bankCardNumber.substring(0, 6);
        switch(number){
            case "603799":
                //return const BankCardModel(name: 'بانک ملی', eName: 'melli', color: '5bbfea');
            case "589210":
                //return const BankCardModel(name: 'بانک سپه', eName: 'sepah', color: '0093dd');
            case "627961":
                //return const BankCardModel(name: 'بانک صنعت و معدن', eName: 'sanatmadan', color: '3e5aa5');
            case "603770":
                //return const BankCardModel(name: 'بانک کشاورزی', eName: 'keshavarsi', color: '009641');
            case "628023":
                //return const BankCardModel(name: 'بانک مسکن', eName: 'maskan', color: 'db6158');
            case "627760":
                //return const BankCardModel(name: 'پست بانک', eName: 'postbank', color: '00963f');
            case "502908":
                //return const BankCardModel(name: 'بانک توسعه و تعاون', eName: 'tosehe', color: '0a8992');
            case "627412":
                //return const BankCardModel(name: 'بانک قتصاد نوین', eName: 'eghtesad', color: '97199a');
            case "622106":
                //return const BankCardModel(name: 'بانک پارسیان', eName: 'parsian', color: 'a10f1f');
            case "502229":
                //return BankCardModel(name: 'بانک پاسارگاد', eName: 'pasargad', color: AppColors.black.toString());
            case "627488":
                //return const BankCardModel(name: 'بانک کاآفرین', eName: 'karafarin', color: '286d46');
            case "621986":
                //return const BankCardModel(name: 'بانک سامان', eName: 'saman', color: '006fb8');
            case "639346":
                //return const BankCardModel(name: 'بانک سینا', eName: 'sina', color: '16479d');
            case "639607":
                //return const BankCardModel(name: 'بانک سرمایه', eName: 'sarmaye', color: '6a578f');
            case "502806":
                //return const BankCardModel(name: 'بانک شهر', eName: 'shahr', color: 'ec3237');
            case "502938":
                //return const BankCardModel(name: 'بانک دی', eName: 'day', color: '008a9f');
            case "603769":
                //return const BankCardModel(name: 'بانک صادرات', eName: 'saderat', color: '29166f');
            case "610433":
                //return const BankCardModel(name: 'بانک ملت', eName: 'mellat', color: 'd12236');
            case "627353":
                //return const BankCardModel(name: 'بانک تجارت', eName: 'tejarat', color: '009ee3');
            case "589463":
                //return const BankCardModel(name: 'بانک رفاه', eName: 'refah', color: '00477a');
            case "627381":
                //return const BankCardModel(name: 'بانک انصار', eName: 'ansar', color: '9a8133');
            case "639370":
                //return const BankCardModel(name: 'بانک مهر اقتصاد', eName: 'mehreqtesad', color: '00a652');
            case "639599":
                //return const BankCardModel(name: 'بانک قوامین', eName: 'ghavamin', color: '0f8a43');
            case "504172":
                //return const BankCardModel(name: 'بانک رسالت', eName: 'resalat', color: '009aae');
            case "636214":
                //return const BankCardModel(name: 'بانک آینده', eName: 'ayandeh', color: '008aae');

                return true;
            default:
                //return const BankCardModel(name: '', eName: '', color: '');
                return false;
        }
    }

    public static Boolean validateBankCardExpire(String expire) {
        if(expire.length()!=6)
            return false;
        List<String> result=CardUtils.getBankCardExpireAsList(expire);
        if(result.size()!=2)
            return false;

        return true;
    }

    public static List<String> getBankCardExpireAsList(String expire) {
        List<String> result=new ArrayList<>();
        Integer m= Utils.parsInt(expire.substring(expire.length()-2,expire.length()));
        Integer y= Utils.parsInt(expire.substring(0,expire.length()-2));
        if(m>0) {
            String mm=Utils.fillLeftBy(m.toString(),2,'0');
            result.add(mm.substring(mm.length()-2,mm.length()));
        }
        if(y>0) {
            String yy = Utils.fillLeftBy(y.toString(), 4, '0');
            result.add(yy.substring(yy.length() - 2, yy.length()));
        }
        return result;
    }

}
