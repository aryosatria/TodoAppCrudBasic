package com.aryosatria.todoappcrudbasic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var cekData: DatabaseReference
    private lateinit var readDataListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseRef = FirebaseDatabase.getInstance().reference
        btn_tambah.setOnClickListener {
            val nama = input_nama.text.toString()
            if (nama.isBlank()){
                toastData("Kolom Nama Harus Diisi")
            } else {
                tambahData(nama)
            }
        }

        btn_hapus.setOnClickListener {
            val nama = input_nama.text.toString()

            if (nama.isBlank()){
                toastData("Kolom Kalimat Harus Diisi")
            } else {
                hapusData(nama)
            }
        }

        btn_update.setOnClickListener {
            val kalimatAwal = input_nama.text.toString()
            val kalimatUpdate = edit_nama.text.toString()
            if (kalimatAwal.isBlank() || kalimatUpdate.isBlank()){
                toastData("Kolom tidak boleh kosong")
            } else {
                updateData(kalimatAwal, kalimatUpdate)
            }
        }
        cekDataKalimat()
    }

    private fun updateData(kalimatAwal: String, kalimatUpdate: String) {
      val dataUpdate = HashMap<String, Any>()
        dataUpdate["Nama"] = kalimatUpdate

        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0){
                    databaseRef.child("Daftar Nama")
                        .child(kalimatAwal)
                        .updateChildren(dataUpdate)
                        .addOnCompleteListener { task->
                            if (task.isSuccessful) toastData("Data Berhasil Diupdate")
                        }
                } else {
                    toastData("Data Yang dituju tidak ada di dalam database")
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }

        val dataAsal = databaseRef.child("Daftar Nama")
            .child(kalimatAwal)
        dataAsal.addListenerForSingleValueEvent(dataListener)
    }

    private fun hapusData(nama: String) {
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.childrenCount > 0){
                  databaseRef.child("Daftar Nama").child(nama)
                      .removeValue()
                      .addOnCompleteListener { task->
                          if (task.isSuccessful) toastData("$nama telah dihapus")
                      }
              } else {
                  toastData("Tidak ada data  $nama")
              }
            }

            override fun onCancelled(p0: DatabaseError) {
                toastData("tidak bisa menghapus data tersebut")
            }
        }
        val cekData = databaseRef.child("Daftar Nama")
            .child(nama)

        cekData.addListenerForSingleValueEvent(dataListener)
    }

    private fun cekDataKalimat() {
        readDataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0){
                    var textData = ""
                    for (data in snapshot.children){
                        val nilai = data.getValue(ModelNama::class.java) as ModelNama
                        textData += "${nilai.Nama} \n"
                    }

                    txt_nama.text = textData
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }

        cekData = databaseRef.child("Daftar Nama")
        cekData.addValueEventListener(readDataListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        cekData.removeEventListener(readDataListener)
    }

    private fun tambahData(kalimat: String) {
        val data = HashMap<String, Any>()
        data["Nama"] = kalimat

        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount < 1) {
                    val tambahData = databaseRef.child("Daftar Nama")
                        .child(kalimat)
                        .setValue(data)
                    tambahData.addOnCompleteListener { task->
                        if (task.isSuccessful){
                            toastData("$kalimat telah ditambakan dalam database")
                        } else {
                            toastData("$kalimat gagal ditambahkan")
                        }
                    }
                } else{
                    toastData("Data tersebut sudah ada di database")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toastData("Terjadi error saat menambah data")
            }
        }

        databaseRef.child("Daftar Nama")
            .child(kalimat).addListenerForSingleValueEvent(dataListener)
    }

    private fun toastData(pesan: String) {
        Toast.makeText(this,pesan, Toast.LENGTH_SHORT).show()
    }

}