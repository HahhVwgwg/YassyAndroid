package kz.yassy.taxi.common;

public interface Constants {

    interface RIDE_REQUEST {
        String DEST_ADD = "d_address";
        String DEST_LAT = "d_latitude";
        String DEST_LONG = "d_longitude";
        String SRC_ADD = "s_address";
        String SRC_LAT = "s_latitude";
        String SRC_LONG = "s_longitude";
        String PAYMENT_MODE = "payment_mode";
        String CARD_ID = "card_id";
        String CARD_LAST_FOUR = "card_last_four";
        String DISTANCE_VAL = "distance";
        String SERVICE_TYPE = "service_type";
        String ESTIMATED_FARE = "estimated_fare";
    }

    interface BroadcastReceiver {
        String INTENT_FILTER = "INTENT_FILTER";
    }

    interface Language {
        String ENGLISH = "en";
        String ARABIC = "ar";
    }

    interface MeasurementType {
        String KM = "Kms";
        String MILES = "miles";
    }

    interface Status {
        String EMPTY = "EMPTY"; // нет заказа, создание
        String MAP = "MAP"; // выбор адреса на карте
        String SERVICE = "SERVICE"; // выбор тарифа и опций
        String SEARCHING = "SEARCHING"; // поиск авто
        String PAYMENT = "PAYMENT";
        String SOS = "SOS";
        String STARTED = "STARTED"; // воитель принял заказ
        String ARRIVED = "ARRIVED";  // водитель подъехал
        String PICKED_UP = "PICKEDUP"; // клиент сел в машину
        String DROPPED = "DROPPED";
        String COMPLETED = "COMPLETED";
        String RATING = "RATING";
    }

    interface InvoiceFare {
        String MINUTE = "MIN";
        String HOUR = "HOUR";
        String DISTANCE = "DISTANCE";
        String DISTANCE_MIN = "DISTANCEMIN";
        String DISTANCE_HOUR = "DISTANCEHOUR";
    }

    interface PaymentMode {
        String CASH = "CASH";
        String CARD = "CARD";
        String PAYPAL = "PAYPAL";
        String WALLET = "WALLET";
        String BRAINTREE = "BRAINTREE";
        String PAYUMONEY = "PAYUMONEY";
        String PAYTM = "PAYTM";

        //TODO ALLAN - Alterações débito na máquina e voucher
        String DEBIT_MACHINE = "DEBIT_MACHINE";
        String VOUCHER = "VOUCHER";
    }

    interface LocationActions{
        String SELECT_SOURCE ="select_source";
        String SELECT_DESTINATION ="select_destination";
        String CHANGE_DESTINATION ="change_destination";
        String SELECT_HOME ="select_home";
        String SELECT_WORK ="select_work";
    }
}
