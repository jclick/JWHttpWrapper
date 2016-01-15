package cn.jclick.demo;

import java.io.Serializable;

/**
 * Created by XuYingjian on 16/1/15.
 */
public class Location implements Serializable{
    private String area;
    private String areaId;
    private String city;
    private String cityId;
    private String country;
    private String countryId;
    private String county;
    private String countyId;
    private String ip;
    private String isp;
    private String ispId;
    private String region;
    private String regionId;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getIspId() {
        return ispId;
    }

    public void setIspId(String ispId) {
        this.ispId = ispId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "Location{" +
                "area='" + area + '\'' +
                ", areaId='" + areaId + '\'' +
                ", city='" + city + '\'' +
                ", cityId='" + cityId + '\'' +
                ", country='" + country + '\'' +
                ", countryId='" + countryId + '\'' +
                ", county='" + county + '\'' +
                ", countyId='" + countyId + '\'' +
                ", ip='" + ip + '\'' +
                ", isp='" + isp + '\'' +
                ", ispId='" + ispId + '\'' +
                ", region='" + region + '\'' +
                ", regionId='" + regionId + '\'' +
                '}';
    }
}
