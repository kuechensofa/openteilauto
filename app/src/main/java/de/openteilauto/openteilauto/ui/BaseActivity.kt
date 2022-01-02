package de.openteilauto.openteilauto.ui

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.ui.search.SearchActivity

abstract class BaseActivity<T : BaseViewModel>: AppCompatActivity() {
    protected abstract var model: T?

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_search -> {
                startSearchActivity()
                true
            }
            R.id.menu_item_logout -> {
                model?.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun startSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }
}