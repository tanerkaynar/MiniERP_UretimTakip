using System;
using System.Configuration;
using System.Data;
using System.Net.Http;
using System.Text;
using Newtonsoft.Json;

namespace NexusERP.Helpers
{
    public static class ApiClient
    {
        private static readonly HttpClient client = new HttpClient();
        private static readonly string BaseUrl;

        static ApiClient()
        {
            string url = ConfigurationManager.AppSettings["ApiUrl"];
            if (string.IsNullOrWhiteSpace(url))
            {
                url = "http://localhost:8080";
            }
            BaseUrl = url.TrimEnd('/');
        }

        public static string Get(string endpoint)
        {
            try
            {
                var response = client.GetAsync($"{BaseUrl}{endpoint}").Result;
                response.EnsureSuccessStatusCode();
                return response.Content.ReadAsStringAsync().Result;
            }
            catch (Exception ex)
            {
                throw new Exception($"API Baglanti Hatasi (GET {endpoint}): {ex.Message}", ex);
            }
        }

        public static DataTable GetDataTable(string endpoint)
        {
            string json = Get(endpoint);
            return JsonConvert.DeserializeObject<DataTable>(json);
        }

        public static string Post(string endpoint, object data)
        {
            try
            {
                string json = JsonConvert.SerializeObject(data);
                var content = new StringContent(json, Encoding.UTF8, "application/json");
                var response = client.PostAsync($"{BaseUrl}{endpoint}", content).Result;
                
                if (!response.IsSuccessStatusCode)
                {
                    string errContent = response.Content.ReadAsStringAsync().Result;
                    throw new Exception(ParseError(errContent, response.StatusCode));
                }
                
                return response.Content.ReadAsStringAsync().Result;
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message, ex);
            }
        }

        public static string Put(string endpoint, object data)
        {
            try
            {
                string json = JsonConvert.SerializeObject(data);
                var content = new StringContent(json, Encoding.UTF8, "application/json");
                var response = client.PutAsync($"{BaseUrl}{endpoint}", content).Result;
                
                if (!response.IsSuccessStatusCode)
                {
                    string errContent = response.Content.ReadAsStringAsync().Result;
                    throw new Exception(ParseError(errContent, response.StatusCode));
                }
                
                return response.Content.ReadAsStringAsync().Result;
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message, ex);
            }
        }

        public static string Delete(string endpoint)
        {
            try
            {
                var response = client.DeleteAsync($"{BaseUrl}{endpoint}").Result;
                if (!response.IsSuccessStatusCode)
                {
                    string errContent = response.Content.ReadAsStringAsync().Result;
                    throw new Exception(ParseError(errContent, response.StatusCode));
                }
                return response.Content.ReadAsStringAsync().Result;
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message, ex);
            }
        }

        private static string ParseError(string content, System.Net.HttpStatusCode statusCode)
        {
            if (string.IsNullOrWhiteSpace(content))
                return GetDefaultErrorMessage(statusCode);

            try
            {
                var errObj = JsonConvert.DeserializeObject<dynamic>(content);
                if (errObj != null)
                {
                    if (errObj.message != null && errObj.message.ToString() != "No message available")
                    {
                        return errObj.message.ToString();
                    }
                    if (errObj.error != null)
                    {
                        return errObj.error.ToString();
                    }
                }
            }
            catch
            {
                
            }

            return GetDefaultErrorMessage(statusCode);
        }

        private static string GetDefaultErrorMessage(System.Net.HttpStatusCode statusCode)
        {
            switch (statusCode)
            {
                case System.Net.HttpStatusCode.BadRequest:
                    return "Geçersiz istek gönderildi. Lütfen bilgileri kontrol edin.";
                case System.Net.HttpStatusCode.Unauthorized:
                    return "Kimlik doğrulama başarısız. Lütfen tekrar giriş yapın.";
                case System.Net.HttpStatusCode.Forbidden:
                    return "Bu işlemi gerçekleştirmek için yetkiniz bulunmuyor.";
                case System.Net.HttpStatusCode.NotFound:
                    return "İstenen kaynak sunucuda bulunamadı.";
                case System.Net.HttpStatusCode.InternalServerError:
                    return "Sunucuda bir iç hata oluştu. Lütfen daha sonra tekrar deneyin.";
                default:
                    return $"Sunucu hatası oluştu (Durum Kodu: {(int)statusCode}).";
            }
        }
    }
}