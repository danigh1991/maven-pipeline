package com.core.datamodel.model.view;

import com.core.model.veiw.BaseJsonView;

public class MyJsonView extends BaseJsonView{

    ////////////////// Store //////////////////////////////
    public interface StoreShortList extends ResponseView{}
    public interface StoreList extends StoreShortList, ServiceBundleShortList, ResponseView{}
    public interface StoreDetails extends StoreList{}

    public interface AdminStoreList extends StoreList, ResponseView{}
    public interface StoreForApproved extends ResponseView{}
    public interface FullStoreForApproved extends  StoreForApproved,StoreBranchForApproved,DiscountForApproved,BranchProductListView {}


    ////////////////// User //////////////////////////////
    public interface UserInfoList extends  ResponseView{}
    public interface UserInfoView extends CityList, UserInfoList{}
    public interface UserInfoViewFull extends UserInfoView, RoleShortList{}

    ////////////////// Permission //////////////////////////////
    public interface PermissionList extends  ResponseView{}
    public interface PermissionDetail extends PermissionList{}

    ////////////////// Activity //////////////////////////////
    public interface ActivityList extends  ResponseView{}
    public interface ActivityDetail extends ActivityList,PermissionList{}

    ////////////////// Role //////////////////////////////
    public interface RoleShortList extends  ResponseView{}
    public interface RoleList extends  RoleShortList{}
    public interface RoleDetail extends RoleList,ActivityList{}


    ///////////////////Discount/////////////////////////////
    public interface DisShortListView extends ResponseView{}
    public interface DisListView extends ResourceListView,DisShortListView,MapView,StoreList,BusinessLeafView,BrandListView,ResponseView{}
    public interface DisListPanelView extends DisListView{}
    public interface DisListSiteView extends DisListView{}
    public interface DiscountForApproved extends ResponseView{}


    /////////////////////StoreBranch/////////////////////////
    public interface StoreBranchContactView extends ResponseView{}
    public interface StoreBranchVeryShortListView extends ResponseView{}
    public interface StoreBranchShortListView extends StoreBranchVeryShortListView{}
    public interface StoreBranchListView extends StoreBranchShortListView,CityList,ResponseView{}
    public interface StoreBranchDetailsView extends TimeTableView,CityView,RegionView,StoreBranchListView,BusinessBrandIdView,StoreBranchBusinessTypeList,ShipmentCostList,StoreBranchHolidayList,StoreBranchBusinessTypeTagList, StoreBranchAdditionalAttributeView,ResponseView{}

    public interface StoreBranchDefaultInfoPanelView extends TimeTableView,BusinessBrandIdView,StoreBranchBusinessTypeList,StoreBranchBusinessTypeTagList,ResponseView{}

    public interface StoreBranchForApproved extends CityList,RegionList,ResponseView{}

    public interface StoreBranchBusinessTypeList extends BusinessTypeShortList,ResponseView{}

    public interface TmpStoreBranchDetailsView extends StoreBranchDetailsView,StoreList{}

    /////////////////////StoreBranchExtend/////////////////////////
    public interface StoreBranchExtendList extends StoreBranchListView,CityList,ResponseView{}
    public interface StoreBranchExtendDetails extends StoreBranchExtendList,GeneralKeyValue{}

    /////////////////////StoreBranchBusinessTypeTag/////////////////////////
    public interface StoreBranchBusinessTypeTagList extends BusinessTypeShortList,ResponseView{}

    /////////////////////Brand/////////////////////////
    public interface BrandShortListView extends ResponseView{}
    public interface BrandListView extends BrandShortListView{}
    public interface BrandDetailsView extends BrandListView,CountryList {}

    /////////////////////Product Type /////////////////////////////////////
    public interface PoductTypeListView extends ResponseView{}

    /////////////////////Product /////////////////////////////////////
    public interface ProductListView extends BusinessTypeShortList,PoductTypeListView,StoreBranchShortListView,/*ProductPackageListView,*/ResponseView{}
    public interface ProductDetailsView extends BrandListView,ResourceListView,ProductListView {}

    public interface ProductForApproved extends ResponseView{}

    public interface ProductChangeState extends ResponseView{}

    public interface BranchProductListView extends BusinessTypeShortList,PoductTypeListView,StoreBranchShortListView,ResponseView{}
    public interface BranchProductDetailsView extends BrandListView,ResourceListView,BranchProductListView {}

    /////////////////////Product Additional Attribute Value/////////////////////////////////////
    public interface ProductAdditionalAttributeView extends ResponseView{}

    /////////////////////Product Package /////////////////////////////////////
    public interface ProductPackageListView extends EventList,ResponseView{}
    public interface ProductPackageListViewWithResource extends ProductPackageListView,ResourceListView {}

    public interface ProductPackageDetailsView extends EventShortDetails,PackageBusinessTypeAttributeListView, BusinessTypeShortList, ProductListView, BrandListView, ProductPackageListViewWithResource,ProductAdditionalAttributeView,BasePriceAmountList,PriceCalcTypeList,ProductPackagePricingFactorView {}

    public interface ProductPackageChangeState extends ResponseView{}

    public interface BranchPackageListView extends EventList,StoreBranchPackageSeasonView,ResponseView{}
    public interface BranchPackageDetailsView extends EventShortDetails,PackageBusinessTypeAttributeListView, BusinessTypeShortList, BranchPackageListView, BrandListView, ProductPackageListViewWithResource,ProductAdditionalAttributeView,BasePriceAmountList,PriceCalcTypeList,ProductPackagePricingFactorView {}


    /////////////////////Product Package BusinessType Attribute/////////////////////////////////////
    public interface PackageBusinessTypeAttributeListView extends BusinessTypeAttribListView,BusinessTypeAttribValidValView,ProductAttribValidValView, ResponseView{}
    public interface PackageBusinessTypeAttributeDetailsView extends PackageBusinessTypeAttributeListView {}


    /////////////////////Business Type Wrapper/////////////////////

    public interface BusinessTypeWrapperShortListView extends ResponseView{}
    public interface BusinessTypeWrapperListView extends BusinessTypeWrapperShortListView{}
    public interface BusinessTypeWrapperParentListView extends BusinessTypeWrapperListView{}
    public interface BusinessTypeWrapperMainListView extends BusinessTypeWrapperListView{}
    public interface BusinessTypeWrapperLeafListView extends BusinessTypeWrapperListView{}
    public interface BusinessTypeWrapperMainAndLeafListView extends BusinessTypeWrapperMainListView,BusinessTypeWrapperLeafListView{}

    public interface BusinessTypeWrapperAllMainAndLeafDeepListView extends BusinessTypeWrapperMainAndLeafListView{}
    public interface BusinessTypeWrapperAllNotLogicalMainAndLeafDeepListView extends BusinessTypeWrapperMainAndLeafListView{}
    public interface BusinessTypeWrapperActiveNotLogicalMainAndLeafDeepListView extends BusinessTypeWrapperMainAndLeafListView{}
    public interface BusinessTypeWrapperActiveNotLogicalMainDeepListView extends BusinessTypeWrapperMainListView{}

     //Verified for Branch By Contract
     public interface BusinessTypeWrapperVerifiedMainListView extends BusinessTypeWrapperListView{}
     public interface BusinessTypeWrapperVerifiedLeafListView extends BusinessTypeWrapperListView{}
     public interface BusinessTypeWrapperVerifiedMainAndLeafListView extends BusinessTypeWrapperVerifiedMainListView,BusinessTypeWrapperVerifiedLeafListView {}

    public interface BusinessTypeWrapperDetailsView extends BusinessTypeWrapperListView,BusinessTypeWrapperMainAndLeafListView,BusinessTypeWrapperVerifiedMainAndLeafListView {}

    /////////////////////Business Type/////////////////////
    public interface BusinessTypeShortList extends ResponseView{}
    public interface BusinessTypeList extends BusinessTypeShortList, ResponseView{}
    public interface BusinessBrandIdView extends ResponseView{}
    public interface BusinessBrandView extends BusinessLeafView,BusinessBrandIdView{}
    public interface BusinessParentView extends ResponseView{}
    public interface BusinessParentMapView extends MapView,BusinessParentView{}
    public interface BusinessLeafView extends BusinessParentView{}
    public interface BusinessTypeView extends BusinessBrandView,BusinessTypeList,PriceCalcTypeList,BasePriceGroupList,ExtraServiceShortList,ServiceResourceTypeList{}

    /////////////////////Attribute/////////////////////
    public interface AttributeShortListView extends ResponseView{}
    public interface AttributeListView extends AttributeShortListView,AttributeValidValListView,ResponseView{}
    public interface AttributeDetailsView extends AttributeListView {}
    public interface AttributeValidValListView extends ResponseView{}
    /////////////////////Business Type Attribute/////////////////////
    public interface BusinessTypeAttribValidValView extends BrandListView, ResponseView{}
    public interface ProductAttribValidValView extends BrandListView, ResponseView{}


    public interface BusinessTypeAttribListView extends ResponseView{}
    public interface BusinessTypeAttributeView extends BusinessTypeAttribListView, BusinessTypeAttributeGroupShortListView,AttributeListView,BusinessLeafView,BusinessTypeAttribValidValView{}

    /////////////////////Business Type Attribute Group/////////////////////
    public interface BusinessTypeAttributeGroupShortListView extends ResponseView {}
    public interface BusinessTypeAttributeGroupListView extends BusinessTypeAttributeGroupShortListView, AttributeListView {}
    public interface BusinessTypeAttributeGroupDetailsView extends BusinessTypeAttributeGroupListView {}


    /////////////////////Brand BusinessType Valid Model/////////////////////
    public interface BrandBusinessTypeValidModelView extends ResponseView{}


    /////////////////////Contract/////////////////////
    public interface ContractListView extends ResponseView{}
    public interface ContractDetailsView extends AttributeListView,ContractStoreBranchBusinessTypeView, StoreShortList,ContractListView {}

    /////////////////////ContractStoreBranchBusinessType/////////////////////
    public interface ContractStoreBranchBusinessTypeView extends StoreBranchListView,BusinessTypeList,ResponseView{}

    /////////////////////Province/////////////////////
    public interface ProvinceList extends CityList,ResponseView{}
    public interface ProvinceDetails extends ProvinceList {}

    /////////////////////City/////////////////////
    public interface CityList extends ResponseView{}
    public interface CityView extends CityList{}
    /////////////////////Region/////////////////////
    public interface RegionList extends ResponseView{}
    public interface RegionView extends RegionList {}
    /////////////////////Country/////////////////////
    public interface CountryList extends ResponseView{}

    /////////////////////Map View /////////////////
    public interface MapView extends ResponseView{}
    public interface PupUpMapView extends ResponseView{}


    public interface DayView extends ResponseView{}
    public interface TimeTableView extends DayView,ResponseView{}
    ///////////////////CouponType////////////////////
    public interface CouponTypeList extends ResponseView{}
    public interface CouponTypeDetails extends CouponTypeList,ResponseView{}

    ///////////////////Coupon//////////////////////////
    public interface CouponList extends CouponTypeList,ResponseView{}
    public interface CouponDetails extends DiscountItemPkgGrpList, ResourceListView, CouponList{}

    public interface CouponWrapperShortView extends ResponseView{}
    public interface CouponWrapperView extends CouponWrapperShortView, ResponseView{}

    public interface CouponChangeState extends ResponseView{}

    ///////////////////Coupon Item//////////////////////////
    public interface CouponItemList extends CouponList,ResponseView{}
    public interface CouponItemDetails extends CouponItemList,CouponDetails{}


    ///////////////////OrderSourceType////////////////////
    public interface OrderSourceTypeList extends ResponseView{}
    public interface OrderSourceTypeDetails extends OrderSourceTypeList,ResponseView{}

    ///////////////////OrderItem//////////////////////////
    public interface OrderItemsList extends OrderSourceTypeList,OrderItemRejectList,ResponseView{}
    public interface OrderItemsDetails extends OrderItemsList,OrderItemRejectDetails {}

    ///////////////////OrderItemDetail//////////////////////////
    public interface OrderItemDetail extends ResponseView{}

    ///////////////////ReserveOrderItem//////////////////////////
    public interface ReserveOrderItemList extends OrderItemsList,ServiceResourceList,BranchPackageSeasonList,TypeWrapperDetail,ResponseView{}


    ///////////////////OrderItemReject//////////////////////////
    public interface OrderItemRejectList extends ResponseView{}
    public interface OrderItemRejectDetails extends OrderItemRejectList {}

    public interface OrderItemRejectWrapperList extends ResponseView{}
    public interface OrderItemRejectWrapperDetails extends OrderItemRejectWrapperList {}
    ///////////////////Order//////////////////////////
    public interface OrderList extends OrderItemsList,ResponseView{}
    public interface OrderDetails extends OrderList,OrderItemsDetails{}

    /////////////////Discount Comment//////////////////
    public interface DiscountCommentDetails extends ResponseView{}
    public interface MyStoreCommentDetails extends DiscountCommentDetails,StoreBranchListView{}

    ///////////////Bank//////////////////////////////////
    public interface BankList extends ResponseView{}
    public interface BankDetails extends BankList{}



    ///////////////Transaction //////////////////////////////////
    public interface BankPaymentList extends BankList,ResponseView{}
    public interface BankPaymentDetails extends BankPaymentList {}


    ///////////////ServiceType //////////////////////////////////
    public interface ServiceTypeList extends ResponseView{}
    public interface ServiceTypeDetails extends ServiceTypeList {}

    ///////////////SiteService //////////////////////////////////
    public interface SiteServiceList extends ServiceTypeList,ResponseView{}
    public interface SiteServiceDetails extends ServiceTypeDetails,SiteServiceList {}

    ///////////////ViolationType //////////////////////////////////
    public interface ViolationTypeList extends ResponseView{}
    public interface ViolationTypeDetails extends ViolationTypeList {}

    ///////////////StoreBranchViolation //////////////////////////////////
    public interface StoreBranchViolationList extends ViolationTypeList, StoreBranchListView,ResponseView{}
    public interface StoreBranchViolationDetails extends StoreBranchViolationList {}

    ///////////////SiteVisitStatisticsWrapper //////////////////////////////////
    public interface SiteVisitStatisticsview2 extends ResponseView{}
    public interface SiteVisitStatisticsview2_2 extends SiteVisitStatisticsview2,ResponseView{}
    public interface SiteVisitStatisticsview3 extends SiteVisitStatisticsview2,ResponseView{}
    public interface SiteVisitStatisticsview4 extends SiteVisitStatisticsview3,ResponseView{}
    public interface SiteVisitStatisticsview6 extends SiteVisitStatisticsview4,ResponseView{}


    ///////////////ContactUs //////////////////////////////////
    public interface ContactUsList extends ResponseView{}
    public interface ContactUsDetails extends ContactUsList{}

    ///////////////ArticleCat //////////////////////////////////
    public interface ArticleCatList extends ResponseView{}
    public interface ArticleCatDetails extends ArticleCatList {}

    ///////////////ArticleType //////////////////////////////////
    public interface ArticleTypeList extends ResponseView{}
    public interface ArticleTypeDetails extends ArticleTypeList {}
    ///////////////Article //////////////////////////////////
    public interface ArticleShortList extends ResponseView{}
    public interface ArticleList extends ArticleShortList,ArticleCatList,ArticleTypeList{}
    public interface ArticleAssignDetails extends ArticleShortList {}
    public interface ArticleDetails extends ArticleList,ArticleAssignDetails {}

    public interface ArticleWrapperList extends ResponseView{}

    ///////////////// Comment//////////////////
    public interface CommentDetails extends ResponseView{}


    ///////////////FileManagerAction //////////////////////////////////
    public interface FileManagerActionList extends ResponseView{}
    public interface FileManagerActionDetails extends FileManagerActionList {}


    ///////////////Panel Menu //////////////////////////////////
    public interface PanelMenu extends ResponseView{}

    ///////////////LinkBuilding//////////////////////////////////
    public interface LinkBuildingList extends ResponseView{}
    public interface LinkBuildingDetails extends LinkBuildingList{}

    ///////////////////HeadBarGroup////////////////////
    public interface PageGroupShortList extends ResponseView{}
    public interface PageGroupList extends PageGroupShortList{}
    public interface PageGroupDetails extends PageGroupList{}


    ///////////////Page //////////////////////////////////
    public interface PageListView extends PageGroupShortList,ResponseView{}
    public interface PageDetailsView extends PageListView {}


    //////////////////////Event Type ///////////////////////////
    public interface EventTypeList extends ResponseView{}
    public interface EventTypeDetails extends EventTypeList {}


    //////////////////////Event ///////////////////////////////

    public interface EventList extends EventTypeList, UserInfoList,ResponseView{}
    public interface EventShortDetails extends EventList {}
    public interface EventDetails extends EventShortDetails {}


    ////////////////////////ProductWrapper /////////////////////////////
    public interface ProductWrapperListView extends ResourceWrapperView,ResponseView{}

    ////////////////////////ProductWrapper /////////////////////////////
    public interface BranchProductPanelWrapper extends BranchPackagePanelWrapper,AdditionalAttributeValueWrapper,AttributeValueWrapperShortView,ResponseView{}

    ////////////////////////ProductPackageWrapper /////////////////////////////
    public interface ProductPackageWrapperView extends AttributeValueWrapperView,BranchPackageExtraServiceWrapperView,ResponseView{}

    ////////////////////////ProductPackageWrapper /////////////////////////////
    public interface BranchPackagePanelWrapper extends AttributeValueWrapperShortView,ResponseView{}

    ////////////////////////ResourceWrapper /////////////////////////////
    public interface ResourceWrapperView extends ResponseView{}
    public interface ResourceWrapperEditingView extends ResourceWrapperView{}

    ////////////////////////ImaginableAttributePackageWrapper /////////////////////////////
    public interface ImaginableAttributePackageWrapperView extends ResponseView,ResourceWrapperEditingView{}

    ////////////////////////AttributeValueWrapper /////////////////////////////
    public interface AttributeValueWrapperShortView extends ResponseView{}
    public interface AttributeValueWrapperView extends AttributeValueWrapperShortView,ResponseView{}

    ////////////////////////AdditionalAttributeValueWrapper /////////////////////////////
    public interface AdditionalAttributeValueWrapper extends ResponseView{}

    ///////////////PaymentType //////////////////////////////////
    public interface PaymentTypeList extends ResponseView{}
    public interface PaymentTypeDetails extends PaymentTypeList {}

    //////////////ShipmentType //////////////////////////////////
    public interface ShipmentTypeList extends ResponseView{}
    public interface ShipmentTypeDetails extends ShipmentTypeList {}

    //////////////ShipmentPlan //////////////////////////////////
    public interface ShipmentPlanList extends ResponseView{}
    public interface ShipmentPlanDetails extends ShipmentPlanList {}


    //////////////TransmissionBaseTiming //////////////////////////////////
    public interface TransmissionBaseTimingList extends ResponseView{}
    public interface TransmissionBaseTimingDetails extends TransmissionBaseTimingList {}

    //////////////TransmissionPlan //////////////////////////////////
    public interface TransmissionPlanList extends ResponseView{}
    public interface TransmissionPlanDetails extends TransmissionPlanList {}


    //////////////    StoreBranchTransmissionLocation //////////////////////////////////
    public interface StoreBranchTransmissionLocationShortList extends DayView,ResponseView{}
    public interface StoreBranchTransmissionLocationShortListWithPolygon extends StoreBranchVeryShortListView,StoreBranchTransmissionLocationShortList{}
    public interface StoreBranchTransmissionLocationList extends StoreBranchVeryShortListView,StoreBranchTransmissionLocationShortList{}
    public interface StoreBranchTransmissionLocationDetails extends StoreBranchTransmissionLocationList{}

    //////////////    StoreBranchTransmissionTimesheet //////////////////////////////////
    public interface StoreBranchTransmissionTimesheetView extends TransmissionBaseTimingList,StoreBranchTransmissionLocationShortList,ResponseView{}

    //////////////    StoreBranchShipmentConfig //////////////////////////////////
    public interface StoreBranchShipmentConfigView extends StoreBranchShortListView,ResponseView{}

    //////////////    Draft Order //////////////////////////////////
    public interface DraftOrderCountAndSumAmountView extends ResponseView{}


    //////////////    GeneralKeyValue  //////////////////////////////////
    public interface GeneralKeyValue extends ResponseView{}

    //////////////    GeneralBankObject  //////////////////////////////////

    public interface GeneralBankObject extends GeneralKeyValue{}

    //////////////    PreFinalOrderWrapper  //////////////////////////////////
    public interface PreFinalOrderWrapperPaymentView extends GeneralBankObject,ResponseView{}




    public interface PreFinalOrderWrapperView extends PreFinalOrderWrapperItemView,DraftOrderCountAndSumAmountView,ResponseView{}
    public interface PreFinalOrderWrapperItemView extends BranchPackageSeasonList,ResponseView{}
    public interface PreFinalOrderWrapperItemViewComplete extends PreFinalOrderWrapperItemView,BranchPackageServiceResourceWrapperView{}

    public interface PreFinalOrderWrapperPaymentTypeView extends PreFinalOrderWrapperView,PreFinalOrderWrapperItemViewComplete,PaymentTypeWrapperView{}

    ///////////////////PaymentTypeWrapper////////////////////
    public interface PaymentTypeWrapperView extends TypeWrapperDetail,ResponseView{}


    ///////////////////PriceCalcType////////////////////
    public interface PriceCalcTypeList extends ResponseView{}
    public interface PriceCalcTypeDetail extends PriceCalcTypeList{}

    ///////////////////BasePriceGroup////////////////////
    public interface BasePriceGroupList extends ResponseView{}
    public interface BasePriceGroupDetail extends BasePriceGroupList{}

    ///////////////////BasePriceAmount////////////////////
    public interface BasePriceAmountList extends ResponseView{}
    public interface BasePriceAmountDetail extends BasePriceAmountList{}


    ///////////////////InitPriceConfigWrapper////////////////////
    public interface PackageInitConfigWrapperView extends PriceCalcTypeList,BasePriceAmountList{}


    ///////////////////ProductPackagePricingFactor////////////////////
    public interface ProductPackagePricingFactorView extends ResponseView{}

    ///////////////////SliderGroup////////////////////
    public interface SliderGroupShortList extends ResponseView{}
    public interface SliderGroupList extends SliderGroupShortList{}
    public interface SliderGroupDetails extends SliderGroupList{}

    ///////////////////LinkedBoxGroup////////////////////
    public interface LinkedBoxGroupShortList extends ResponseView{}
    public interface LinkedBoxGroupList extends LinkedBoxGroupShortList{}
    public interface LinkedBoxGroupDetails extends LinkedBoxGroupList{}

    ///////////////////LinkedBox////////////////////
    public interface LinkedBoxShortList extends ResponseView{}
    public interface LinkedBoxList extends LinkedBoxShortList{}
    public interface LinkedBoxDetails extends LinkedBoxList{}


    public interface LinkedBoxWrapperShortList extends ResponseView{}
    public interface LinkedBoxWrapperList extends LinkedBoxWrapperShortList{}
    public interface LinkedBoxWrapperExternalList extends LinkedBoxWrapperShortList{}
    public interface LinkedBoxWrapperDetails extends LinkedBoxWrapperList{}
    ///////////////////Tag////////////////////
    public interface TagList extends ResponseView{}
    public interface TagDetails extends TagList{}

    ///////////////////Faq////////////////////
    public interface FaqList extends ResponseView{}
    public interface FaqDetails extends FaqList{}


    ///////////////////Business Dashboard////////////////////
    public interface BusinessDashboard extends ResponseView{}

    ///////////////////DiscountItem Packaging Group////////////////////
    public interface DiscountItemPkgGrpList extends ResponseView{}


    ///////////////////Ticket////////////////////
    public interface TicketList extends ResponseView{}
    public interface TicketDetails extends TicketList{}

    ///////////////////Configuration////////////////////
    public interface ConfigurationView extends ResponseView{}


    ///////////////////Dynamic List Generator////////////////////
    public interface ListGeneratorParamList extends ResponseView{}
    public interface ListGeneratorParamDetails extends ListGeneratorParamList, ParamValue{}
    ///////////////////SiteTheme ////////////////////
    public interface SiteThemeList extends ResponseView{}
    public interface SiteThemeDetails extends SiteThemeList{}

    ///////////////////HeadBarGroup////////////////////
    public interface HeadBarGroupShortList extends ResponseView{}
    public interface HeadBarGroupList extends HeadBarGroupShortList{}
    public interface HeadBarGroupDetails extends HeadBarGroupList{}


    ///////////////////HeadBar////////////////////
    public interface HeadBarShortList extends HeadBarGroupShortList, ResponseView{}
    public interface HeadBarList extends HeadBarShortList{}
    public interface HeadBarDetails extends HeadBarList, HeadBarGroupDetails{}

    ///////////////////ShipmentCost////////////////////
    public interface ShipmentCostList extends ShipmentCostDetailList, ResponseView{}
    public interface ShipmentCostDetails extends ShipmentCostList{}

    ///////////////////ShipmentCost////////////////////
    public interface ShipmentCostDetailList extends ShipmentTypeList, ResponseView{}
    public interface ShipmentCostDetailDetails extends ShipmentCostDetailList{}


    ///////////////////ParamGroup////////////////////
    public interface ParamGroupShortList extends ResponseView{}
    public interface ParamGroupList extends ParamGroupShortList{}
    public interface ParamGroupDetails extends ParamGroupList{}


    ///////////////////Param////////////////////
    public interface ParamShortList extends ParamGroupShortList, ResponseView{}
    public interface ParamList extends ParamShortList{}
    public interface ParamDetails extends ParamList{}


    ///////////////////DisCodeType////////////////////
    public interface DisCodeTypeShortList extends ResponseView{}
    public interface DisCodeTypeList extends DisCodeTypeShortList{}
    public interface DisCodeTypeDetails extends DisCodeTypeList{}


    ///////////////////DisCode////////////////////
    public interface DisCodeShortList extends DisCodeTypeShortList, ResponseView{}
    public interface DisCodeList extends DisCodeShortList{}
    public interface DisCodeDetails extends DisCodeList{}

    ///////////////////DisCodeItem////////////////////
    public interface DisCodeItemList extends DisCodeShortList,ResponseView{}
    public interface DisCodeItemDetails extends  DisCodeDetails,DisCodeItemList{}


    ///////////////////Presenter////////////////////
    public interface PresenterShortList extends ResponseView{}
    public interface PresenterList extends PresenterShortList{}
    public interface PresenterDetails extends PresenterList{}


    ///////////////////Presenter Contract////////////////////
    public interface PresenterContractShortList extends PresenterShortList,ThemeTypeList,ResponseView{}
    public interface PresenterContractList extends PresenterContractShortList{}
    public interface PresenterContractDetails extends PresenterContractList{}


    ///////////////////Presenter////////////////////
    public interface ThemeTypeList extends ResponseView{}
    public interface ThemeTypeDetails extends ThemeTypeList{}
    ///////////////////ParamValue////////////////////
    public interface ParamValue extends ParamShortList, ResponseView{}



    ///////////////////ServiceBundle////////////////////
    public interface ServiceBundleShortList extends ResponseView{}
    public interface ServiceBundleList extends ServiceBundleShortList{}
    public interface ServiceBundleDetails extends ServiceBundleList{}


    ///////////////////StoreBranch Holiday////////////////////
    public interface StoreBranchHolidayList extends  ResponseView{}
    public interface StoreBranchHolidayDetails extends  ResponseView{}


    ///////////////////PwaConfig////////////////////
    public interface PwaConfigShortList extends ResponseView{}
    public interface PwaConfigList extends PwaConfigShortList{}
    public interface PwaConfigDetails extends PwaConfigList{}

    ///////////////////MessageResource////////////////////
    public interface MessageResourceView extends ResponseView{}


    ///////////////////NotifyRequest////////////////////

    public interface NotifyRequestShortList extends ResponseView{}
    public interface NotifyRequestList extends NotifyRequestShortList{}
    public interface NotifyRequestDetails extends NotifyRequestList{}

    ///////////////////MessageBox////////////////////
    public interface MessageBoxWrapperList extends ResponseView{}
    public interface MessageBoxWrapperDetail extends MessageBoxWrapperList{}


    ///////////////////FinanceDestNumber////////////////////
    public interface FinanceDestNumberList extends ResponseView{}
    public interface FinanceDestNumberDetails extends FinanceDestNumberList {}


    ///////////////////ExtraServiceGroup////////////////////
    public interface ExtraServiceGroupList extends ResponseView{}
    public interface ExtraServiceGroupDetails extends ExtraServiceGroupList {}

    ///////////////////ExtraService////////////////////
    public interface ExtraServiceShortList extends ResponseView{}
    public interface ExtraServiceList extends ExtraServiceGroupList, ExtraServiceShortList{}
    public interface ExtraServiceDetails extends ExtraServiceList,ExtraServiceValidValList {}

    ///////////////////ExtraServiceValidVal////////////////////
    public interface ExtraServiceValidValList extends StoreBranchExtraServiceValidValView,ResponseView{}
    public interface ExtraServiceValidValDetails extends ExtraServiceValidValList {}

    ///////////////////StoreBranchExtraService////////////////////
    public interface StoreBranchExtraServiceList extends ResponseView{}
    public interface StoreBranchExtraServiceDetails extends ExtraServiceList,StoreBranchExtraServiceList,StoreBranchExtraServiceValidValView{}

    ///////////////////StoreBranchExtraService////////////////////
    public interface StoreBranchExtraServiceValidValView extends ResponseView{}




    /////////////////////StoreBranch Additional Attribute Value/////////////////////////////////////
    public interface StoreBranchAdditionalAttributeView extends ResponseView{}


    ///////////////////Season ////////////////////
    public interface SeasonList extends ResponseView{}
    public interface SeasonDetails extends SeasonList{}

    ///////////////////StoreBranchPackageSeason ////////////////////
    public interface StoreBranchPackageSeasonView extends StoreBranchPackageSeasonSalePlanView,ResponseView{}

    ///////////////////StoreBranchPackage Season Sale Plan ////////////////////
    public interface StoreBranchPackageSeasonSalePlanView extends ResponseView{}


    ///////////////////StoreBranchPackageInventory ////////////////////
    public interface StoreBranchPackageInventoryView extends ResponseView{}

    ///////////////////StoreBranchPackagePurchasedDetail ////////////////////
    public interface StoreBranchPackageLiveBookingView extends ResponseView{}




    //////////////////////User Group ///////////////////////////
    public interface UserGroupList extends ResponseView{}
    public interface UserGroupDetails extends UserGroupList {}


    //////////////////////ServiceResourceType ///////////////////////////
    public interface ServiceResourceTypeList extends ResponseView{}
    public interface ServiceResourceTypeDetails extends ServiceResourceTypeList {}


    //////////////////////ServiceResource ///////////////////////////
    public interface ServiceResourceList extends ResponseView{}
    public interface ServiceResourceDetails extends ServiceResourceTypeList,ServiceResourceList {}

    //////////////////////StoreBranchPackageServiceResource ///////////////////////////
    public interface StoreBranchPackageServiceResourceList extends ResponseView{}

    //////////////////////FeedBack ///////////////////////////
    public interface FeedBackDetail extends ResponseView{}


    //////////////////////UserDeviceMetadata ///////////////////////////
    public interface UserDeviceMetadataView extends ResponseView{}


    //////////////////////BranchPackageExtraServiceWrapperView ///////////////////////////
    public interface BranchPackageExtraServiceWrapperView extends ResponseView{}


    //////////////////////BranchPackageSeason ///////////////////////////
    public interface BranchPackageSeasonList extends ResponseView{}
    public interface BranchPackageSeasonDetails extends BranchPackageSeasonList{}


    ///////////////////AdditionalInfo////////////////////
    public interface AdditionalOrderInfoList extends ResponseView{}
    public interface AdditionalOrderInfoDetails extends AdditionalOrderInfoList {}

    ///////////////////BranchPackageServiceResourceWrapper////////////////////
    public interface BranchPackageServiceResourceWrapperView extends ResponseView{}

    //////////////////////BranchPackageSeasonAndResourceWrapper ///////////////////////////
    public interface BranchPackageSeasonAndResourceWrapperView extends BranchPackageServiceResourceWrapperView,BranchPackageSeasonList,ResponseView{}


    ///////////////////ExternalApi////////////////////
    public interface ExternalApiList extends ResponseView{}
    public interface ExternalApiDetails extends ExternalApiList {}

    ///////////////////ExternalServiceCall////////////////////
    public interface ExternalApiCallList extends ResponseView{}
    public interface ExternalApiCallDetails extends ExternalApiCallList {}


}
