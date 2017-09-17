# Soil

### 概述

 - 本项目已经是对android基础框架工程的第二个版本，第一个版本中对网络部分的封装并不是很简洁，第二版本中参考了一些优秀的项目对网络部分进行了优化封装并去掉了Dagger2部分，因为考虑到RxJava2部分就已经足够让一些人感觉到困惑了，如果增加Dagger，反而会提高框架使用的门槛。所以，本项目只是基于基于RxJava2+Retrofit2打造。

 - 主要功能包含网络(上传、下载、缓存)、权限管理、图片加载等。基本都是项目中必用功能，模块间解耦，可拓展性强。

 - 本项目目前没有上传类似JCenter这样的仓库中心，暂时只能本地依赖。后面会提供网络依赖坐标。

### 项目结构

 * 本项目有三个Module
 - app Module是本项目的Sample工程主要是常用功能的使用示例和一些常用的UI上的通用部分。
 - soil Module就是这个基础框架的所有功能部分。下一节做详细的分包说明。
 - xsoil Module是对soil的一个扩展，只是这里有一些功能还没有想好如何抽象成通用的部分，为了保持基础工程的功能纯净，有了这个扩展的模块。比如每个小组的服务端返回的数据的结构都不同，这些需要随手变化。
 
### 功能说明

 在soil Module下现在有10个包目录，每个包体现的是不同的功能，其他9个相互独立，但是都依赖Utils包，下面做一个主要功能包的说明。

 - cache包 主要是最数据缓存的封装，包括内存LRU缓存，磁盘缓存，磁盘LRU缓存以及SP操作，这也是使用最频繁的。
 - event包下面是类似于EventBus之类的事件线工具包，使用RxJava实现，我们没必要到处去发广播通知事件，使用这个包下的功能即可，并且简洁易于管理。
 - loader包提供一个图片加载的基础实现接口，默认提供Glide实现，但是不提供Glide包的依赖，需要你使用的时候自己添加依赖，也可以自定义实现ILoader接口方便统一图片加在的入口，解决将来在进行图片加在工具切换的时候只有一个入口修改即可，不需要到处去修改图片加在部分，详细的使用参考app部分。
 - mvp包下面是我对mvp的一个泛型封装，如果你喜欢这种模式，直接实现这里面的公共约束接口即可。
 - utils包 主要是常用的工具比如像素密度转换，手机状态，网络状态，文集操作，系统通知，加解密算法等功能，都很常用，但是根绝业务场景不同不会全部使用到，但大部分都会用到。
 - ui包下面的主要是一些基础页面和常用控件的封装，将来我会删除此部分，因为我越来越觉得此部分意义不大。
 - http包下就是最核心的网络部分了。网络算是项目的灵魂，基本每个项目都离不开网络，这里提供一个使用简单，支持定制常用配置，如各种拦截器、缓存策略、请求头等的网络库封装。原始的Retrofit请求网络时需要每个接口都写一个服务接口，这样非常不便利。项目中采用泛型转换方式，将响应结果ResponseBody通过Rxjava的map操作符转换成需要的T，具体实现参考项目中http包下的func包，如果需要Http响应码，也可以将响应结果包装成Response<ResponseBody>这样进行转换成T，但是项目中很少需要Http响应码来进行判定，一般使用服务器自定义的响应码就可以了，故该模块采用ResponseBody统一接收这种处理方式。
 - router部分 这个部分可能过几天我就会删除，这是我在最初接触组件化开发的时候对页面路由的一个设想的封装，最初觉得通过结仇抽象实现页面跳转，现在想来通过注解的形式向服务端那样通过url路由服务的形式来路由页面才是最好的。现在也有很多的开源的路由方案实现，比如ARouter和Router都很好，后续我也会参考这两个实现实现自己的路由框架。
 
### 使用说明
 最简单的使用就是依赖soil部分就可以了，我们也会提供aar包，后面提供网络依赖坐标，这样也便于管理版本升级切换。
 
#### 网络请求
- 请求支持全局配置和单个请求的局部配置，如果局部配置与全局配置冲突，那么局部配置会替换全局配置。

- 全局配置支持`CallAdapter.Factory`、`Converter.Factory`、`okhttp3.Call.Factory`、`SSLSocketFactory`、`HostnameVerifier`、`ConnectionPool`、主机URL、请求头、请求参数、代理、拦截器、Cookie、OKHttp缓存、连接超时时间、读写超时时间、失败重试次数、失败重试间隔时间的一系列配置。

- 局部请求配置支持主机URL、请求后缀、请求头、请求参数、拦截器、本地缓存策略、本地缓存时间、本地缓存key、连接超时时间、读写超时时间的一系列配置。

- 支持OKHttp本身的Http缓存，也支持外部自定义的在线离线缓存，可配置缓存策略，共有五种缓存策略，如优先获取缓存策略，具体实现参考http包下的strategy包。

- 支持请求与响应统一处理，不需要上层每个模块都定义请求服务接口。

- 支持泛型T接收处理响应数据，也可根据服务器返回的统一数据模式定制如包含Code、Data、Message的通用Model ApiResult<T>。由于ApiResult的属性不定，无法做到统一处理，所以单独放到xsoil module中，里面包含与其相关的请求处理，可以根据该module定制属于各自服务器的相关功能。

- 支持异常统一处理，定制了ApiException拦截处理，统一返回异常信息。

- 支持返回Observable，可继续定制请求的相关特性，也支持返回回调的处理结果。

- 支持失败重试机制，可配置失败重试次数以及重试时间间隔。

- 支持根据Tag中途取消请求，也可以取消所有请求。

#### 使用示例：

第一步需要在application中进行全局初始化以及添加全局相关配置，具体使用如下：

```
private void initNet() {
        SoilHttp.init(this);
        SoilHttp.config()
                //配置请求主机地址
                .baseUrl("http://gank.io/")
                //配置全局请求头
                .globalHeaders(new HashMap<String, String>())
                //配置全局请求参数
                .globalParams(new HashMap<String, String>())
                //配置读取超时时间，单位秒
                .readTimeout(30)
                //配置写入超时时间，单位秒
                .writeTimeout(30)
                //配置连接超时时间，单位秒
                .connectTimeout(30)
                //配置请求失败重试次数
                .retryCount(3)
                //配置请求失败重试间隔时间，单位毫秒
                .retryDelayMillis(1000)
                //配置是否使用cookie
                .setCookie(true)
                //配置自定义cookie
//                .apiCookie(new ApiCookie(this))
                //配置是否使用OkHttp的默认缓存
                .setHttpCache(true)
                //配置OkHttp缓存路径
//                .setHttpCacheDirectory(new File(SoilHttp.getContext().getCacheDir(), SoilConfig.CACHE_HTTP_DIR))
                //配置自定义OkHttp缓存
//                .httpCache(new Cache(new File(SoilHttp.getContext().getCacheDir(), SoilConfig.CACHE_HTTP_DIR), SoilConfig.CACHE_MAX_SIZE))
                //配置自定义离线缓存
//                .cacheOffline(new Cache(new File(SoilHttp.getContext().getCacheDir(), SoilConfig.CACHE_HTTP_DIR), SoilConfig.CACHE_MAX_SIZE))
                //配置自定义在线缓存
//                .cacheOnline(new Cache(new File(SoilHttp.getContext().getCacheDir(), SoilConfig.CACHE_HTTP_DIR), SoilConfig.CACHE_MAX_SIZE))
                //配置开启Gzip请求方式，需要服务器支持
//                .postGzipInterceptor()
                //配置应用级拦截器
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY))
                //配置网络拦截器
//                .networkInterceptor(new NoCacheInterceptor())
                //配置转换工厂
                .converterFactory(GsonConverterFactory.create())
                //配置适配器工厂
                .callAdapterFactory(RxJava2CallAdapterFactory.create())
                //配置请求工厂
//                .callFactory(new Call.Factory() {
//                    @Override
//                    public Call newCall(Request request) {
//                        return null;
//                    }
//                })
                //配置连接池
//                .connectionPool(new ConnectionPool())
                //配置主机证书验证
                .hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier("http://api.club.lenovo.cn/"))
                //配置SSL证书验证
                .SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null))
                //配置主机代理
//                .proxy(new Proxy(Proxy.Type.HTTP, new SocketAddress() {}))
                ;

    }

```
后面就是具体调用请求的过程，请求的类型有多种情形，下面就以最常用的几种类型举例说明，具体效果可以查看demo，以下为使用示例：

 - GET 不带缓存

```
public void getBanner() {
        if(mView!=null) {
            mView.showWaitDailog();
            SoilHttp.get("/api/data/福利/5/1")
                    .request(new ACallback<ResultData<Banner>>() {
                        @Override
                        public void onSuccess(ResultData<Banner> data) {
                            if (mView != null) {
                                mView.showBanners(data);
                                mView.hideWaitDailog();
                            }
                        }

                        @Override
                        public void onFail(int errCode, String errMsg) {
                            if (mView != null) {
                                mView.hideWaitDailog();
                                ErrorMsg error = new ErrorMsg();
                                error.setErrorCode(errCode + "");
                                error.setErrorMessage(errMsg);
                                mView.showError(error);
                            }
                        }
                    });
        }
    }
    
```

提供了多种参数的添加方式，具体可查看BaseRequest中提供的API。

 - GET 带缓存

```
SoilHttp.GET("getAuthor")
        .setLocalCache(true)//设置是否使用缓存，如果使用缓存必须设置为true
        .cacheMode(CacheMode.FIRST_CACHE) //配置缓存策略
        .request(new ACallback<CacheResult<Banner>>() {
            @Override
            public void onSuccess(CacheResult<Banner> cacheResult) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

由于带缓存方式有点不一样，需要告知上层是否是缓存数据，所以需要外部包裹一层CacheResult结构，使用时必须要按照这种方式设置model，还有需要注意的是必须要设置缓存开关为true，如果为false是没法解析CacheResult结构的，这点一定切记。

 - GET 返回String

```
SoilHttp.GET("getString").request(new ACallback<String>() {
    @Override
    public void onSuccess(String data) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

 - GET 返回List

```
public void getDataItems(int pageNum, int pageSize) {
        if(mView!=null){
            mView.showWaitDailog();

            SoilHttp.get("/api/data/Android/"+pageSize+"/"+pageNum)
                    .request(new ACallback<ResultData<NewsItem>>() {
                        @Override
                        public void onSuccess(ResultData<NewsItem> data) {
                            if (mView != null) {
                                mView.showDataItems(data);
                                mView.hideWaitDailog();
                            }
                        }

                        @Override
                        public void onFail(int errCode, String errMsg) {
                            if (mView != null) {
                                mView.hideWaitDailog();
                                ErrorMsg error = new ErrorMsg();
                                error.setErrorCode(errCode + "");
                                error.setErrorMessage(errMsg);
                                mView.showError(error);
                            }
                        }
                    });
        }
    }
```

 - POST 上传表单

```
SoilHttp.base(new ApiPostRequest("postFormAuthor")
        .addForm("author_name", getString(R.string.author_name))
        .addForm("author_nickname", getString(R.string.author_nickname))
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

上传表单时需要通过addForm将键值对一个个添加进去，支持上传中文字符。

 - POST 上传json

```
AuthorModel mAuthorModel = new AuthorModel();
mAuthorModel.setAuthor_id(1008);
mAuthorModel.setAuthor_name(getString(R.string.author_name));
mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
SoilHttp.base(new ApiPostRequest("postJsonAuthor")
        .setJson(GSONUtil.gson().toJson(mAuthorModel)))
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

上传JSON格式数据时需要先将数据转换成JSON格式，再通过setJson添加进去。

 - POST 后缀带请求参数

```
AuthorModel mAuthorModel = new AuthorModel();
mAuthorModel.setAuthor_id(1009);
mAuthorModel.setAuthor_name(getString(R.string.author_name));
mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
SoilHttp.base(new ApiPostRequest("postUrlAuthor")
        .addUrlParam("appId", "10001")
        .addUrlParam("appType", "Android")
        .setJson(GSONUtil.gson().toJson(mAuthorModel)))
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

有些POST请求可能URL后面也带有参数，这样的话需要通过addUrlParam进行设置，与添加到请求body的参数设置方式addParam是不一样的，这点需要注意。

#### 上传下载

该库提供的上传下载功能比较简洁实用，基本能满足单个线程下的常用相关操作，如果需要多线程和断点续传功能就需要上层实现，也可以依赖如RxDownload库。

 - 支持单文件和多文件上传。

 - 支持每个文件都有对应的回调进度。

 - 支持传入字节流或者字节数组进行上传。

 - 支持下载进度回调，每秒刷新下载进度。


#### 缓存

包含内存、磁盘二级缓存以及SharedPreferences缓存，可自由拓展。磁盘缓存支持KEY加密存储，可定制缓存时长。SharedPreferences支持内容安全存储，采用Base64加密解密。

 - 内存存储：`MemoryCache.getInstance().put("authorInfo", mAuthorModel);`

 - 内存获取：`MemoryCache.getInstance().get("authorInfo");`

 - 磁盘缓存存储：`diskCache.put("authorInfo", mAuthorModel);`

 - 磁盘缓存获取：`diskCache.get("authorInfo");`

 - SharedPreferences缓存存储：`spCache.put("authorInfo", mAuthorModel);`

 - SharedPreferences缓存获取：`spCache.get("authorInfo");`


#### 事件总线

采用Rx响应式编程思想建立的RxBus模块，采用注解方式标识事件消耗地，通过遍历查找事件处理方法。支持可插拔，可替换成EventBus库，只需上层采用的同样是注解方式，那么上层是不需要动任何代码的。

 - 发送事件：`BusFactory.getBus().post(new AuthorEvent().setAuthorModel(mAuthorModel));`

 - 注册事件：`BusFactory.getBus().register(this);`

 - 取消注册：`BusFactory.getBus().unregister(this);`

 - 接收事件：

```
@EventSubscribe
public void showAuthor(IEvent event) {
    if (event != null && event instanceof AuthorEvent) {
        Log.i("Receive Event Message:" + ((AuthorEvent) event).getAuthorModel());
    }
}
```

 如果需要定制使用其他Bus如EventBus，那么只需将实现IBus接口的对象在应用初始化时通过`BusFactory.setBus(new EventBus())`传进去即可。


#### 图片加载

采用Glide库进行图片加载，支持轻量级图片加载，该模块支持可插拔，可根据需求替换成任意图片加载库，如果项目中对于图片处理要求比较高，那么可以替换成Facebook提供的Fresco库。

 - 初始化：在application中进行如下初始化操作：`LoaderFactory.getLoader().init(this);`

 - 调用过程：

```
LoaderManager.getLoader().loadResource((ImageView) helper.getView(R.id.ivPic), R.mipmap.ic_logo, null);
```
如果需要定制使用其他图片加载框架如Fresco，那么只需将实现ILoader接口的对象在应用初始化时通过`LoaderFactory.setLoader(new FrescoLoader())`传进去即可。


### 最后
这里不仅是在自己的项目中实践和总结，还大量的参考了第三方开源的项目，特此感谢。此项目很多功能也许并不全面，还有很多的冗余功能，在后续的优化维护中会不断的改进。

