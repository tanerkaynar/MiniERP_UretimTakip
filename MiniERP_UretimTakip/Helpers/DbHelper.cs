using System.Data;
using System.Data.SqlClient;

namespace MiniERP_UretimTakip.Helpers
{
    public static class DbHelper
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        public static SqlConnection GetConnection()
        {
            return new SqlConnection(ConnectionString);
        }

        public static DataTable GetDataTable(string query)
        {
            using (var connection = GetConnection())
            {
                var adapter = new SqlDataAdapter(query, connection);
                var table = new DataTable();
                adapter.Fill(table);
                return table;
            }
        }

        public static int ExecuteNonQuery(string query, params SqlParameter[] parameters)
        {
            using (var connection = GetConnection())
            using (var command = new SqlCommand(query, connection))
            {
                if (parameters != null)
                    command.Parameters.AddRange(parameters);

                connection.Open();
                return command.ExecuteNonQuery();
            }
        }

        public static object ExecuteScalar(string query, params SqlParameter[] parameters)
        {
            using (var connection = GetConnection())
            using (var command = new SqlCommand(query, connection))
            {
                if (parameters != null)
                    command.Parameters.AddRange(parameters);

                connection.Open();
                return command.ExecuteScalar();
            }
        }
    }
}
