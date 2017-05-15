package fourthquadrant.chileme.bean;

import java.util.List;

import fourthquadrant.chileme.bean.Model.BannerModel;
import fourthquadrant.chileme.bean.Model.RefreshModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Euphoria on 2017/3/18.
 */

public interface Engine {

    @GET("{itemCount}item.json")
    Call<BannerModel> fetchItemsWithItemCount(@Path("itemCount") int itemCount);

    @GET
    Call<List<RefreshModel>> loadContentData(@Url String url);
}