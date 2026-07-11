namespace NexusERP.Models
{
    public class SiparisDetayi
    {
        public int SiparisDetayID { get; set; }
        public int SiparisID { get; set; }
        public int UrunID { get; set; }
        public int Miktar { get; set; }
        public decimal BirimFiyat { get; set; }
    }
}